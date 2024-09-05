package io.mipt.typeeleven.core.service.spi;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ChatDaoSpi {
    Flux<Integer> findChatsForUser(int userId);
    Flux<Integer> findActiveUsersForChat(int chatId);
    Mono<Integer> createEmptyChat();
    Mono<Void> setActiveUsersForChat(int chatId, List<Integer> users);
}
