package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "read_statuses",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "channel_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class ReadStatus {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(name = "last_read_at", nullable = false)
    private Instant lastReadAt = Instant.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    public ReadStatus(User user, Channel channel) {
        this.user = user;
        this.channel = channel;
    }

    public void markAsRead() {
        Instant now = Instant.now();
        if (lastReadAt != null && now.isBefore(lastReadAt)) return;
        lastReadAt = now;
        updatedAt = now;
    }

    public UUID getUserId() {
        return user != null ? user.getId() : null;
    }

    public UUID getChannelId() {
        return channel != null ? channel.getId() : null;
    }
}