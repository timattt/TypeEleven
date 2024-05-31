package org.shlimtech.typeeleven.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.shlimtech.typeeleven.domain.model.Chat;
import org.shlimtech.typeeleven.domain.model.Message;
import org.shlimtech.typeeleven.service.core.ChattedMessengerService;
import org.shlimtech.typeeleven.service.core.exception.MessengerException;
import org.shlimtech.typeeleven.service.impl.repository.ChatRepository;
import org.shlimtech.typeeleven.service.impl.repository.MessageRepository;
import org.shlimtech.typesixbusinesslogic.domain.model.User;
import org.shlimtech.typesixbusinesslogic.service.impl.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Log
@Service
@RequiredArgsConstructor
public class ChattedMessengerServiceImpl implements ChattedMessengerService {
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Flux<Chat> listChats(int userId) {
        List<Chat> chats = chatRepository.findByActiveUsersContains(userId);
        return Flux.fromIterable(chats);
    }

    @Override
    @Transactional
    public Flux<User> listAvailableUsers() {
        List<User> users = userRepository.findAll();
        return Flux.fromIterable(users);
    }

    @Override
    @Transactional
    public Flux<Message> listMessages(int chatId, long fromTime, int count) {
        List<Message> result = messageRepository.findAllByChatEqualsAndTimeLessThan(
                chatRepository.findById(chatId).orElseThrow(() -> new MessengerException("No such chat")),
                fromTime,
                PageRequest.ofSize(count).withSort(Sort.by("time").descending())
        );
        return Flux.fromIterable(result);
    }

    @Override
    @Transactional
    public Mono<Chat> createChat(List<Integer> users) {
        Chat chat = chatRepository.save(Chat.builder().activeUsers(users).build());
        return Mono.just(chat);
    }

    @Override
    @Transactional
    public Mono<Message> newMessage(int senderId, int chatId, String content) {
        Chat chat = chatRepository
                .findById(chatId)
                .orElseThrow(() -> new MessengerException("Chat not found"));
        Message message = Message.builder()
                .content(content)
                .senderId(senderId)
                .chat(chat)
                .time(System.currentTimeMillis())
                .build();
        messageRepository.save(message);
        return Mono.just(message);
    }
}
