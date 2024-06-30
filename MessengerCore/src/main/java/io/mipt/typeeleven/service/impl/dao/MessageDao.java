package io.mipt.typeeleven.service.impl.dao;

import io.mipt.typeeleven.domain.model.Message;
import io.mipt.typeeleven.service.impl.dao.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MessageDao {
    private final MessageRepository messageRepository;
    private final DatabaseClient databaseClient;

    public Flux<Message> selectMessagesChunked(int chatId, long fromTime, int count) {
        return messageRepository.selectMessagesFromTime(chatId, fromTime, count);
    }

    public Mono<Integer> createMessage(int senderId, int chatId, String content) {
        return databaseClient
                .sql("insert into message(content, time, chat_id, sender_id) VALUES (:content, :time, :chatId, :senderId)")
                .bind("content", content)
                .bind("time", System.currentTimeMillis())
                .bind("chatId", chatId)
                .bind("senderId", senderId)
                .filter(statement -> statement.returnGeneratedValues("id"))
                .map(row -> row.get("id", Integer.class))
                .first();
    }

    public Mono<Message> selectMessageById(int id) {
        return messageRepository.selectMessageById(id);
    }
}
