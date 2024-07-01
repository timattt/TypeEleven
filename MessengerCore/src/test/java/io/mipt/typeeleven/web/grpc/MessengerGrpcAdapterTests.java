package io.mipt.typeeleven.web.grpc;

import io.grpc.StatusRuntimeException;
import io.mipt.typeeleven.BaseTest;
import io.mipt.typeeleven.domain.model.Chat;
import io.mipt.typeeleven.domain.model.Message;
import io.mipt.typeeleven.grpc.*;
import io.mipt.typeeleven.service.core.TypeElevenMessengerService;
import io.mipt.typeeleven.web.grpc.mapper.GrpcMapper;
import io.mipt.typesix.businesslogic.domain.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Queue;

import static io.mipt.typeeleven.web.grpc.TestClientConfig.DEFAULT_TOKEN_CLAIM_USER_ID;
import static org.mockito.Mockito.*;

public class MessengerGrpcAdapterTests extends BaseTest {
    @Autowired
    @Qualifier("securedClient")
    private MessengerGrpc.MessengerBlockingStub securedClient;
    @Autowired
    @Qualifier("unsecuredClient")
    private MessengerGrpc.MessengerBlockingStub unsecuredClient;
    @Autowired
    private GrpcMapper grpcMapper;
    @MockBean
    private TypeElevenMessengerService typeElevenMessengerService;
    @Autowired
    @Qualifier("ids")
    private Queue<Integer> ids;

    @Test
    public void listUsersSuccessTest() {
        User user = User.builder().id(1).email("a@gmail.com").build();
        when(typeElevenMessengerService.listAvailableUsers()).thenReturn(Flux.just(user));

        var users = securedClient.listUsers(EmptyRequest.newBuilder().build()).getUsersList();

        verify(typeElevenMessengerService).listAvailableUsers();

        Assertions.assertEquals(List.of(grpcMapper.toGrpcUser(user)), users);
    }

    @Test
    public void listUsersUnauthorizedTest() {
        when(typeElevenMessengerService.listAvailableUsers()).thenReturn(Flux.just());

        Assertions.assertThrows(StatusRuntimeException.class, () -> unsecuredClient.listUsers(EmptyRequest.newBuilder().build()));

        verify(typeElevenMessengerService, never()).listAvailableUsers();
    }

    @Test
    public void listChatsSuccessTest() {
        Chat chat = Chat.builder().id(4).activeUsers(List.of()).build();
        when(typeElevenMessengerService.listChats(anyInt())).thenReturn(Flux.just(chat));

        var chats = securedClient.listChats(EmptyRequest.newBuilder().build()).getChatsList();

        Assertions.assertNotNull(chats);
        Assertions.assertEquals(List.of(grpcMapper.toGrpcChat(chat)), chats);

        verify(typeElevenMessengerService).listChats(eq(DEFAULT_TOKEN_CLAIM_USER_ID));
    }

    @Test
    public void listChatsUnauthorizedTest() {
        Assertions.assertThrows(StatusRuntimeException.class, () -> unsecuredClient.listChats(EmptyRequest.newBuilder().build()));

        verify(typeElevenMessengerService, never()).listChats(anyInt());
    }

    @Test
    public void listMessagesSuccessTest() {
        final int chatId = 244;
        final int senderId = 45;
        final int count = 1;
        final long time = 34567;
        Message message = Message.builder().id(23).senderId(senderId).chatId(chatId).content("biba").build();
        when(typeElevenMessengerService.listMessages(anyInt(), anyLong(), anyInt())).thenReturn(Flux.just(message));

        var messages = securedClient.listMessages(ListMessagesRequest.newBuilder().setChatId(chatId).setCount(count).setFromTime(time).build()).getMessagesList();

        Assertions.assertNotNull(messages);
        Assertions.assertEquals(List.of(grpcMapper.toGrpcMessage(message)), messages);

        verify(typeElevenMessengerService).listMessages(eq(chatId), eq(time), eq(count));
    }

    @Test
    public void listMessagesUnauthorizedTest() {
        final int chatId = 244;
        final int count = 1;
        final long time = 34567;
        Assertions.assertThrows(StatusRuntimeException.class, () -> unsecuredClient.listMessages(ListMessagesRequest.newBuilder().setChatId(chatId).setCount(count).setFromTime(time).build()));

        verify(typeElevenMessengerService, never()).listMessages(anyInt(), anyLong(), anyInt());
    }

    @Test
    public void newChatSuccessTest() {
        final int receiverId = 2344;
        final var users = List.of(DEFAULT_TOKEN_CLAIM_USER_ID, receiverId);
        Chat chat = Chat.builder().id(4).activeUsers(users).build();
        when(typeElevenMessengerService.createChat(any())).thenReturn(Mono.just(chat));

        var createdChat = securedClient.newChat(NewChatRequest.newBuilder().setReceiverId(receiverId).build()).getChat();

        Assertions.assertNotNull(createdChat);
        Assertions.assertEquals(grpcMapper.toGrpcChat(chat), createdChat);

        verify(typeElevenMessengerService).createChat(eq(users));
    }

    @Test
    public void newChatUnauthorizedTest() {
        final int receiverId = 2344;
        Assertions.assertThrows(StatusRuntimeException.class, () -> unsecuredClient.newChat(NewChatRequest.newBuilder().setReceiverId(receiverId).build()));

        verify(typeElevenMessengerService, never()).createChat(any());
    }

    @Test
    public void sendAndReceiveMessageSimpleTest() throws InterruptedException {
        final int chatId = 244;
        final int otherId = 45;
        final int count = 1;
        final long time = 34567;
        final int messageId = 2455;
        final String content = "biba";
        Message message = Message.builder().id(messageId).senderId(DEFAULT_TOKEN_CLAIM_USER_ID).chatId(chatId).content(content).build();

        when(typeElevenMessengerService.newMessage(anyInt(), anyInt(), any())).thenReturn(Mono.just(message));
        when(typeElevenMessengerService.listActiveUsersForMessageChat(eq(messageId))).thenReturn(Flux.just(otherId, DEFAULT_TOKEN_CLAIM_USER_ID));

        ids.add(otherId);
        var iterator = securedClient.receiveMessages(EmptyRequest.newBuilder().build());

        Thread.sleep(1000);

        var sent = securedClient.sendMessage(SendMessageRequest.newBuilder().setContent(content).setChatId(chatId).build()).getMessage();

        var received = iterator.next().getMessage();

        Assertions.assertNotNull(received);
        Assertions.assertEquals(sent, received);
    }

    @Test
    public void sendAndReceiveMessageMessageTimingTest() throws InterruptedException {
        final int firstId = 100;
        final int secondId = 200;
        final int thirdId = 300;

        final var users = List.of(firstId, secondId, thirdId);

        final int chatId = 244;
        final int message1Id = 111;
        final int message2Id = 222;
        final String content = "biba";

        Message message1 = Message.builder().id(message1Id).senderId(firstId).chatId(chatId).content(content).build();
        Message message2 = Message.builder().id(message2Id).senderId(firstId).chatId(chatId).content(content).build();

        when(typeElevenMessengerService.listActiveUsersForMessageChat(anyInt())).thenReturn(Flux.fromIterable(users));

        ids.add(thirdId);
        var iteratorThird = securedClient.receiveMessages(EmptyRequest.newBuilder().build());

        Thread.sleep(1000);

        when(typeElevenMessengerService.newMessage(anyInt(), anyInt(), any())).thenReturn(Mono.just(message1));
        ids.add(firstId);
        var sent = securedClient.sendMessage(SendMessageRequest.newBuilder().setContent(content).setChatId(chatId).build()).getMessage();
        Assertions.assertNotNull(sent);
        Assertions.assertEquals(grpcMapper.toGrpcMessage(message1), sent);

        Thread.sleep(1000);

        ids.add(secondId);
        var iterator = securedClient.receiveMessages(EmptyRequest.newBuilder().build());

        Assertions.assertNotNull(iterator);

        Thread.sleep(1000);

        when(typeElevenMessengerService.newMessage(anyInt(), anyInt(), any())).thenReturn(Mono.just(message2));
        ids.add(firstId);
        sent = securedClient.sendMessage(SendMessageRequest.newBuilder().setContent(content).setChatId(chatId).build()).getMessage();
        Assertions.assertNotNull(sent);
        Assertions.assertEquals(grpcMapper.toGrpcMessage(message2), sent);

        Thread.sleep(1000);

        Assertions.assertTrue(iterator.hasNext());

        var received = iterator.next().getMessage();

        Assertions.assertNotNull(received);
        Assertions.assertEquals(grpcMapper.toGrpcMessage(message2), received);

        Assertions.assertNotNull(iteratorThird);

        var mes1 = iteratorThird.next().getMessage();
        var mes2 = iteratorThird.next().getMessage();

        Assertions.assertNotNull(mes1);
        Assertions.assertNotNull(mes2);

        Assertions.assertEquals(grpcMapper.toGrpcMessage(message1), mes1);
        Assertions.assertEquals(grpcMapper.toGrpcMessage(message2), mes2);
    }

}
