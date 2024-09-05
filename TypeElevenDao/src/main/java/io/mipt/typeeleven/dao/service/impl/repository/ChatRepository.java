package io.mipt.typeeleven.dao.service.impl.repository;

import io.mipt.typeeleven.core.domain.model.Chat;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChatRepository extends R2dbcRepository<Chat, Integer> {
    @Query("select chat.id from chat join chat_active_users on chat.id = chat_active_users.chat_id where chat_active_users.active_user = :userId")
    Flux<Integer> selectChatsForUsers(int userId);
    @Query("select active_user from chat_active_users where chat_id = :chatId")
    Flux<Integer> findAllActiveUsersForChat(int chatId);

    @Query("insert into chat default values")
    Mono<Void> createChat();
    @Query("insert into chat_active_users(chat_id, active_user) VALUES(:chatId, :userId)")
    Mono<Void> insertActiveUser(int chatId, int userId);

    @Query("delete from chat")
    Mono<Void> deleteAll();

    @Query("delete from chat_active_users")
    Mono<Void> deleteAllFromActiveUsers();
}
