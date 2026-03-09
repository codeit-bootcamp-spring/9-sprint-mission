package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_statuses")
@Getter
@Setter
@NoArgsConstructor
public class UserStatus {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id = UUID.randomUUID();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt = Instant.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    public UserStatus(User user) {
        this.user = user;
        this.lastActiveAt = Instant.now();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void touch() {
        this.lastActiveAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public boolean isOnline() {
        return lastActiveAt != null && lastActiveAt.isAfter(Instant.now().minusSeconds(300));
    }

    public UUID getUserId() {
        return user != null ? user.getId() : null;
    }

    public void setUser(User user) {
        this.user = user;
        if (user.getStatus() != this) {
            user.setStatus(this);
        }
    }
}