package io.mipt.typeeleven.service.impl;

import io.mipt.typesix.businesslogic.service.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import io.mipt.typeeleven.domain.model.Chat;
import io.mipt.typeeleven.domain.model.Message;
import io.mipt.typeeleven.service.core.ChattedMessengerService;
import io.mipt.typeeleven.service.core.exception.MessengerException;
import io.mipt.typeeleven.service.impl.repository.ChatRepository;
import io.mipt.typeeleven.service.impl.repository.MessageRepository;
import io.mipt.typesix.businesslogic.domain.model.User;
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
