package io.mipt.typeeleven.service.core;

import io.mipt.typeeleven.domain.model.Chat;
import io.mipt.typeeleven.domain.model.Message;
import io.mipt.typesix.businesslogic.domain.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ChattedMessengerService {
    Flux<Chat> listChats(int userId);
    Flux<User> listAvailableUsers();
    Flux<Message> listMessages(int chatId, long fromTime, int count);
    Mono<Chat> createChat(List<Integer> users);
    Mono<Message> newMessage(int senderId, int chatId, String content);
}
