package io.mipt.typeeleven.core.domain.model;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Table
@Builder
@AllArgsConstructor
public class Message {
    @Id
    private final int id;
    private final String content;
    private final long time;
    private final int chatId;
    private final int senderId;
}
