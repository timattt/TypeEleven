package io.mipt.typeeleven.service.impl.dao;

import io.mipt.typeeleven.service.impl.dao.repository.UserRepository;
import io.mipt.typesix.businesslogic.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class UserDao {
    private final UserRepository userRepository;

    public Flux<User> findAllUsers() {
        return userRepository.selectAllUsers();
    }
}
