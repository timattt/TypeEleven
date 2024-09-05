package io.mipt.typeeleven.dao.service.impl.repository;

import io.mipt.typeeleven.core.domain.model.Message;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MessageRepository extends R2dbcRepository<Message, Integer> {
    @Query("select * from message where chat_id = :chatId and time < :time order by time DESC limit :count")
    Flux<Message> selectMessagesFromTime(int chatId, long time, int count);
    @Query("select * from message where id = :id")
    Mono<Message> selectMessageById(int id);

    @Query("insert into message(content, time, chat_id, sender_id) VALUES (:content, :time, :chatId, :senderId)")
    Mono<Void> createMessage(String content, long time, int chatId, int senderId);

    @Query("delete from message")
    Mono<Void> deleteAll();
}
