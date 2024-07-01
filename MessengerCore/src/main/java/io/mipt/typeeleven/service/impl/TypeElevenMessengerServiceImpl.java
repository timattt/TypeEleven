package io.mipt.typeeleven.service.impl;

import io.mipt.typeeleven.domain.model.Chat;
import io.mipt.typeeleven.domain.model.Message;
import io.mipt.typeeleven.service.core.TypeElevenMessengerService;
import io.mipt.typeeleven.service.core.exception.TypeElevenMessengerException;
import io.mipt.typeeleven.service.impl.dao.ChatDao;
import io.mipt.typeeleven.service.impl.dao.MessageDao;
import io.mipt.typeeleven.service.impl.dao.UserDao;
import io.mipt.typesix.businesslogic.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Log
@Service
@RequiredArgsConstructor
public class TypeElevenMessengerServiceImpl implements TypeElevenMessengerService {
    private final UserDao userDao;
    private final ChatDao chatDao;
    private final MessageDao messageDao;

    @Override
    @Transactional
    public Flux<User> listAvailableUsers() {
        return userDao.findAllUsers();
    }

    @Override
    @Transactional
    public Flux<Chat> listChats(int userId) {
        return chatDao
                .findChatsForUser(userId)
                .flatMap(chatId -> chatDao
                        .findActiveUsersForChat(chatId)
                        .collectList()
                        .map(users -> Chat.builder().id(chatId).activeUsers(users).build())
                )
                .onErrorMap(error -> new TypeElevenMessengerException("error while listing chats", error));
    }

    @Override
    @Transactional
    public Flux<Message> listMessages(int chatId, long fromTime, int count) {
        Assert.isTrue(count > 0, "count must be greater than 0");
        return messageDao
                .selectMessagesChunked(chatId, fromTime, count)
                .onErrorMap(error -> new TypeElevenMessengerException("error while listing messages", error));
    }

    @Override
    @Transactional
    public Mono<Chat> createChat(List<Integer> users) {
        Assert.notNull(users, "users must not be null");
        Assert.isTrue(users.size() >= 2, "users must be at least of size 2");
        return chatDao
                .createEmptyChat()
                .flatMap(chatId -> chatDao
                        .setActiveUsersForChat(chatId, users)
                        .thenReturn(Chat.builder().id(chatId).activeUsers(users).build())
                )
                .onErrorMap(error -> new TypeElevenMessengerException("error while creating chat", error));
    }

    @Override
    @Transactional
    public Mono<Message> newMessage(int senderId, int chatId, String content) {
        Assert.notNull(content, "content must not be null");
        return messageDao
                .createMessage(senderId, chatId, content)
                .map(messageId -> Message
                        .builder()
                        .id(messageId)
                        .content(content)
                        .chatId(chatId)
                        .senderId(senderId)
                        .time(System.currentTimeMillis())
                        .build())
                .onErrorMap(error -> new TypeElevenMessengerException("error while adding message", error));
    }

    @Override
    @Transactional
    public Flux<Integer> listActiveUsersForMessageChat(int messageId) {
        return messageDao
                .selectMessageById(messageId)
                .flatMapMany(message -> chatDao.findActiveUsersForChat(message.getChatId()))
                .onErrorMap(error -> new TypeElevenMessengerException("error while listing active users for chat", error));
    }
}
