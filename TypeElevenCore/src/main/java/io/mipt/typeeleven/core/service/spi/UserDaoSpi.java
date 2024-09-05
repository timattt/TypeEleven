package io.mipt.typeeleven.core.service.spi;

import io.mipt.typesix.businesslogic.domain.model.User;
import reactor.core.publisher.Flux;

public interface UserDaoSpi {
    Flux<User> findAllUsers();
}
