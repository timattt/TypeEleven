package io.mipt.typeeleven.service.impl;

import io.mipt.typeeleven.domain.model.Chat;
import io.mipt.typeeleven.domain.model.Message;
import io.mipt.typeeleven.service.core.TypeElevenMessengerService;
import io.mipt.typeeleven.service.impl.dao.ChatDao;
import io.mipt.typeeleven.service.impl.dao.MessageDao;
import io.mipt.typeeleven.service.impl.dao.UserDao;
import io.mipt.typesix.businesslogic.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        return chatDao.findChatsForUser(userId)
                .flatMap(chatId -> chatDao
                        .findActiveUsersForChat(chatId)
                        .collectList()
                        .map(users -> Chat.builder().id(chatId).activeUsers(users).build())
                );
    }

    @Override
    @Transactional
    public Flux<Message> listMessages(int chatId, long fromTime, int count) {
        return messageDao.selectMessagesChunked(chatId, fromTime, count);
    }

    @Override
    @Transactional
    public Mono<Chat> createChat(List<Integer> users) {
        return chatDao
                .createEmptyChat()
                .flatMap(chatId -> chatDao
                        .setActiveUsersForChat(chatId, users)
                        .thenReturn(Chat.builder().id(chatId).activeUsers(users).build())
                );
    }

    @Override
    @Transactional
    public Mono<Message> newMessage(int senderId, int chatId, String content) {
        return messageDao
                .createMessage(senderId, chatId, content)
                .map(messageId -> Message
                        .builder()
                        .id(messageId)
                        .content(content)
                        .chatId(chatId)
                        .senderId(senderId)
                        .build());
    }

    @Override
    public Flux<Integer> listActiveUsersForMessageChat(int messageId) {
        return messageDao
                .selectMessageById(messageId)
                .flatMapMany(message -> chatDao.findActiveUsersForChat(message.getChatId()));
    }
}
