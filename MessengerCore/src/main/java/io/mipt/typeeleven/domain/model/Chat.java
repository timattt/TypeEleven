package io.mipt.typeeleven.domain.model;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Table
@Builder
@AllArgsConstructor
public class Chat {
    @Id
    private final int id;
    private final List<Integer> activeUsers;
}
