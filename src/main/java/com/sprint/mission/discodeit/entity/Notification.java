package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private UUID receiverId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Instant createdAt;

    public Notification(UUID receiverId, String title, String content) {
        this.receiverId = receiverId;
        this.title = title;
        this.content = content;
        this.createdAt = Instant.now();
    }
}
