package org.shlimtech.typeeleven.service.impl.repository;

import org.shlimtech.typeeleven.domain.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
}
