package io.mipt.typeeleven.service.impl.dao.repository;

import io.mipt.typesix.businesslogic.domain.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Integer> {
    @Query("SELECT id, email, first_name, last_name FROM type6user")
    Flux<User> selectAllUsers();
    @Query("select id, email, first_name, last_name from type6user where email = :email")
    Mono<User> selectByEmail(String email);

    @Query("insert into type6user(email, first_name, last_name, status) values(:email, :firstName, :lastName, 1)")
    Mono<Void> insertUser(String email, String firstName, String lastName);

    @Query("delete from type6user")
    Mono<Void> deleteAll();
}
