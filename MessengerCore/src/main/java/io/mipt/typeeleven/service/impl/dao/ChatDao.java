package io.mipt.typeeleven.service.impl.dao;

import io.mipt.typeeleven.service.impl.dao.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatDao {
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
                .map(row -> row.get("id", Integer.class))
                .first();
    }

    public Mono<Void> setActiveUsersForChat(int chatId, List<Integer> users) {
        return Flux
                .fromIterable(users)
                .flatMap(userId -> chatRepository.insertActiveUser(chatId, userId))
                .then();
    }
}
