package io.mipt.typeeleven.domain.model;

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
    @Column(name = "content")
    private String content;
    @Column(name = "time")
    private long time;
    @ManyToOne
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    private io.mipt.typeeleven.domain.model.Chat chat;
    @Column(name = "sender_id")
    private int senderId;
}
