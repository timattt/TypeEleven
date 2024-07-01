package io.mipt.typeeleven.service.core;

import io.mipt.typeeleven.domain.model.Chat;
import io.mipt.typeeleven.domain.model.Message;
import io.mipt.typeeleven.service.core.exception.TypeElevenMessengerException;
import io.mipt.typesix.businesslogic.domain.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Core service for type-11 messenger.
 */
public interface TypeElevenMessengerService {
    /**
     * Returns list of available chats for user presented by its id.
     *
     * @param userId id if user in database. Referenced to type6user table
     * @return complete Chat object
     * @throws TypeElevenMessengerException If some database manipulation error happens
     */
    Flux<Chat> listChats(int userId) throws TypeElevenMessengerException;

    /**
     * Gives list of all users to which message can be sent.
     *
     * @return List of type6users
     * @throws TypeElevenMessengerException If some database manipulation error happens
     */
    Flux<User> listAvailableUsers() throws TypeElevenMessengerException;

    /**
     * Gives paginated list of messages in chat.
     * Messages are sorted by creation time then selecting first count messages with creation time less then given time.
     *
     * @param chatId Id of the chat to give messages from
     * @param fromTime Time from which messages will be selected, less then this time
     * @param count Amount of messages, greater zero.
     * @return List of messages
     * @throws TypeElevenMessengerException If some database manipulation error happens
     */
    Flux<Message> listMessages(int chatId, long fromTime, int count) throws TypeElevenMessengerException;

    /**
     * Creates new empty chat with list of participants.
     *
     * @param users List of users which acts in this chat, never {@code null}, with minimum size of 2
     * @return Chat object
     * @throws TypeElevenMessengerException If some database manipulation error happens
     */
    Mono<Chat> createChat(List<Integer> users) throws TypeElevenMessengerException;

    /**
     * Creates the new message.
     *
     * @param senderId Id of user which created this message
     * @param chatId Id of chat where message may be sent
     * @param content Text of message, never {@code null}
     * @return Message object
     * @throws TypeElevenMessengerException If some database manipulation error happens
     */
    Mono<Message> newMessage(int senderId, int chatId, String content) throws TypeElevenMessengerException;

    /**
     * Gives list of users participants of chat related to this message.
     *
     * @param messageId Id of message to determine chat and users from it
     * @return List of users participants of chat related to this message
     * @throws TypeElevenMessengerException If some database manipulation error happens
     */
    Flux<Integer> listActiveUsersForMessageChat(int messageId) throws TypeElevenMessengerException;
}
