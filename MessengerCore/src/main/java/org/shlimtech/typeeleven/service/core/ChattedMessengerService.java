package org.shlimtech.typeeleven.service.core;

import org.shlimtech.typeeleven.domain.model.Chat;
import org.shlimtech.typeeleven.domain.model.Message;
import org.shlimtech.typesixbusinesslogic.domain.model.User;

import java.util.List;

public interface ChattedMessengerService {
    List<Chat> listChats(int userId);
    List<User> listAvailableUsers();
    Chat createChat(List<Integer> users);
    Message newMessage(int senderId, int chatId, String content);
}
