package org.shlimtech.typeeleven.web.grpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.lognet.springboot.grpc.GRpcService;
import org.shlimtech.typeeleven.domain.model.Message;
import org.shlimtech.typeeleven.grpc.*;
import org.shlimtech.typeeleven.service.core.ChattedMessengerService;
import org.shlimtech.typeeleven.web.grpc.mapper.GrpcMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Log
@GRpcService
@RequiredArgsConstructor
public class MessengerGrpcAdapter extends MessengerGrpc.MessengerImplBase {
    private final GrpcMapper grpcMapper;
    private final ChattedMessengerService messengerService;
    private final ConcurrentMap<Integer, StreamObserver<ExchangeResponse>> connections = new ConcurrentHashMap<>();
    private final Base64.Decoder decoder = Base64.getUrlDecoder();
    private final ObjectMapper objectMapper;

    @Override
    public void listUsers(EmptyRequest request, StreamObserver<ListUsersResponse> responseObserver) {
        responseObserver.onNext(ListUsersResponse.newBuilder().addAllUsers(messengerService.listAvailableUsers().stream().map(grpcMapper::toGrpcUser).toList()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void listChats(EmptyRequest request, StreamObserver<ListChatsResponse> responseObserver) {
        responseObserver.onNext(ListChatsResponse.newBuilder().addAllChats(messengerService.listChats(getUserId()).stream().map(grpcMapper::toGrpcChat).toList()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void newChat(NewChatRequest request, StreamObserver<NewChatResponse> responseObserver) {;
        responseObserver.onNext(NewChatResponse.newBuilder().setChat(grpcMapper.toGrpcChat(messengerService.createChat(List.of(getUserId(), request.getReceiverId())))).build());
        responseObserver.onCompleted();
    }

    @Override
    public void listMessages(ListMessagesRequest request, StreamObserver<ListMessagesResponse> responseObserver) {
        responseObserver.onNext(ListMessagesResponse.newBuilder().addAllMessages(messengerService.listMessages(request.getChatId(), request.getFromTime(), request.getCount()).stream().map(grpcMapper::toGrpcMessage).toList()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void receiveMessages(EmptyRequest request, StreamObserver<ExchangeResponse> responseObserver) {
        int userId = getUserId();
        if (connections.containsKey(userId)) {
            connections.remove(userId).onCompleted();
        }
        connections.put(userId, responseObserver);
    }

    @Override
    public void sendMessage(SendMessageRequest request, StreamObserver<SendMessageResponse> responseObserver) {
        int senderId = getUserId();
        Message message = messengerService.newMessage(senderId, request.getChatId(), request.getContent());
        Type11Message type11Message = grpcMapper.toGrpcMessage(message);
        responseObserver.onNext(SendMessageResponse.newBuilder().setMessage(type11Message).build());
        responseObserver.onCompleted();

        message.getChat().getActiveUsers().forEach(userId -> {
            if (senderId != userId) {
                var stream = connections.get(userId);
                if (stream != null) {
                    try {
                        stream.onNext(ExchangeResponse.newBuilder().setMessage(type11Message).build());
                    } catch (Exception e) {
                        connections.remove(userId);
                    }
                }
            }
        });
    }

    @SneakyThrows
    private int getUserId() {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String[] chunks = token.split("\\.");
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));

        Map<String, String> headerMap = objectMapper.readValue(header, Map.class);
        Map<String, String> payloadMap = objectMapper.readValue(payload, Map.class);

        return Integer.parseInt(payloadMap.get("id"));
    }
}
