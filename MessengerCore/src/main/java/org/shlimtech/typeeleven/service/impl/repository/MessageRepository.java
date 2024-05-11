package org.shlimtech.typeeleven.service.impl.repository;

import org.shlimtech.typeeleven.domain.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findAllByReceiverEmailAndTimeGreaterThan(String receiverEmail, long time);
    List<Message> findAllBySenderEmailAndTimeGreaterThan(String senderEmail, long time);
    List<Message> findAllByReceiverEmailAndTimeLessThan(String receiverEmail, long time);
    List<Message> findAllBySenderEmailAndTimeLessThan(String senderEmail, long time);
}
