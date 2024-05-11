package org.shlimtech.typeeleven.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.shlimtech.typeeleven.domain.model.Message;
import org.shlimtech.typeeleven.service.core.SimpleMessengerService;
import org.shlimtech.typeeleven.service.impl.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SimpleMessengerServiceImpl implements SimpleMessengerService {
    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public void sendMessage(String senderEmail, String receiverEmail, String message) {
        messageRepository.save(Message.builder()
                .content(message)
                .receiverEmail(receiverEmail)
                .senderEmail(senderEmail)
                .time(System.currentTimeMillis())
                .build());
    }

    @Override
    @Transactional
    public List<Message> getMessagesAfter(long time, String userEmail) {
        List<Message> from = messageRepository.findAllByReceiverEmailAndTimeGreaterThan(userEmail, time);
        List<Message> to = messageRepository.findAllBySenderEmailAndTimeGreaterThan(userEmail, time);
        return Stream.concat(from.stream(), to.stream()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Message> getMessagesBefore(long time, String userEmail, int count) {
        List<Message> from = messageRepository.findAllByReceiverEmailAndTimeLessThan(userEmail, time);
        List<Message> to = messageRepository.findAllBySenderEmailAndTimeLessThan(userEmail, time);
        return Stream.concat(from.stream(), to.stream()).collect(Collectors.toList());
    }
}
