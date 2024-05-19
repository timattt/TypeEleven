package org.shlimtech.typeeleven.service.impl.repository;

import org.shlimtech.typeeleven.domain.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    List<Chat> findByActiveUsersContains(int userId);
}
