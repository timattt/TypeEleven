package org.shlimtech.typeeleven.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Message> messages;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> activeUsers;
}
