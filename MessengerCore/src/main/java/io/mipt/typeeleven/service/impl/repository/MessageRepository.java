package io.mipt.typeeleven.service.impl.repository;

import io.mipt.typeeleven.domain.model.Chat;
import io.mipt.typeeleven.domain.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findAllByChatEqualsAndTimeLessThan(Chat chat, long time, Pageable pageable);
}
