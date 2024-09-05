package io.mipt.typeeleven.dao.service.impl;

import io.mipt.typeeleven.core.service.spi.UserDaoSpi;
import io.mipt.typeeleven.dao.service.impl.repository.UserRepository;
import io.mipt.typesix.businesslogic.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class UserDao implements UserDaoSpi {
    private final UserRepository userRepository;

    public Flux<User> findAllUsers() {
        return userRepository.selectAllUsers();
    }
}
