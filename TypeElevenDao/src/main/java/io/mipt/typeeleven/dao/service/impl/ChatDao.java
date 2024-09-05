package io.mipt.typeeleven.dao.service.impl;

import io.mipt.typeeleven.core.service.spi.ChatDaoSpi;
import io.mipt.typeeleven.dao.service.impl.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatDao implements ChatDaoSpi {
    private final ChatRepository chatRepository;
    private final DatabaseClient databaseClient;

    public Flux<Integer> findChatsForUser(int userId) {
        return chatRepository.selectChatsForUsers(userId);
    }

    public Flux<Integer> findActiveUsersForChat(int chatId) {
        return chatRepository.findAllActiveUsersForChat(chatId);
    }

    public Mono<Integer> createEmptyChat() {
        return databaseClient
                .sql("insert into chat DEFAULT VALUES")
                .filter(statement -> statement.returnGeneratedValues("id"))
                .map(row -> Integer.parseInt(row.get("id").toString()))
                .first();
    }

    public Mono<Void> setActiveUsersForChat(int chatId, List<Integer> users) {
        return Flux
                .fromIterable(users)
                .flatMap(userId -> chatRepository.insertActiveUser(chatId, userId))
                .then();
    }
}
