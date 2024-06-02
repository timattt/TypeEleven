package io.mipt.typeeleven.service.impl.repository;

import io.mipt.typeeleven.domain.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    List<Chat> findByActiveUsersContains(int userId);
}
