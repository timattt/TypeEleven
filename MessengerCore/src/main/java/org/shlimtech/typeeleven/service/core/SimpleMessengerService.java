package org.shlimtech.typeeleven.service.core;

import org.shlimtech.typeeleven.domain.model.Message;

import java.util.List;

public interface SimpleMessengerService {
    void sendMessage(String senderEmail, String receiverEmail, String message);
    List<Message> getMessagesAfter(long time, String userEmail);
    List<Message> getMessagesBefore(long time, String userEmail, int count);
}
