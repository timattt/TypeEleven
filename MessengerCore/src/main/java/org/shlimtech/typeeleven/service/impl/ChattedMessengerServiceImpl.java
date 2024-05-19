package org.shlimtech.typeeleven.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.shlimtech.typeeleven.domain.model.Chat;
import org.shlimtech.typeeleven.domain.model.Message;
import org.shlimtech.typeeleven.service.core.ChattedMessengerService;
import org.shlimtech.typeeleven.service.core.exception.MessengerException;
import org.shlimtech.typeeleven.service.impl.repository.ChatRepository;
import org.shlimtech.typeeleven.service.impl.repository.MessageRepository;
import org.shlimtech.typesixbusinesslogic.domain.model.User;
import org.shlimtech.typesixbusinesslogic.service.impl.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChattedMessengerServiceImpl implements ChattedMessengerService {
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public List<Chat> listChats(int userId) {
        return chatRepository.findByActiveUsersContains(userId);
    }

    @Override
    @Transactional
    public List<User> listAvailableUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public Chat createChat(List<Integer> users) {
        return chatRepository.save(Chat.builder().activeUsers(users).build());
    }

    @Override
    @Transactional
    public Message newMessage(int senderId, int chatId, String content) {
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
        return message;
    }
}
