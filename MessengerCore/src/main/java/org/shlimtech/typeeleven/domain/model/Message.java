package org.shlimtech.typeeleven.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "sender_email")
    private String senderEmail;
    @Column(name = "receiver_email")
    private String receiverEmail;
    @Column(name = "content")
    private String content;
    @Column(name = "time")
    private long time;
}
