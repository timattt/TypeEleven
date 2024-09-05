package io.mipt.typeeleven.core.service.spi;

import io.mipt.typeeleven.core.domain.model.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageDaoSpi {
    Flux<Message> selectMessagesChunked(int chatId, long fromTime, int count);
    Mono<Integer> createMessage(int senderId, int chatId, String content);
    Mono<Message> selectMessageById(int id);
}
