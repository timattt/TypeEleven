package io.mipt.typeeleven.service.core;

import io.mipt.typeeleven.domain.model.Chat;
import io.mipt.typeeleven.domain.model.Message;
import io.mipt.typeeleven.service.core.exception.TypeElevenMessengerException;
import io.mipt.typesix.businesslogic.domain.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TypeElevenMessengerService {
    /**
     * Returns list of available chats for user presented by its id.
     * @param userId id if user in database. Referenced to type6user table.
     * @return complete Chat object.
     * @throws TypeElevenMessengerException If some database manipulation error happens.
     */
    Flux<Chat> listChats(int userId) throws TypeElevenMessengerException;
    Flux<User> listAvailableUsers() throws TypeElevenMessengerException;
    Flux<Message> listMessages(int chatId, long fromTime, int count) throws TypeElevenMessengerException;
    Mono<Chat> createChat(List<Integer> users) throws TypeElevenMessengerException;
    Mono<Message> newMessage(int senderId, int chatId, String content) throws TypeElevenMessengerException;
    Flux<Integer> listActiveUsersForMessageChat(int messageId) throws TypeElevenMessengerException;
}
