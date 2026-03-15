package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Entity
@Table(name = "channels")
public class Channel {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType type;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    protected Channel() {}

    public static Channel createPublic(String name, String description) {
        Channel c = new Channel();
        c.type = ChannelType.PUBLIC;
        c.name = name;
        c.description = description;
        Instant now = Instant.now();
        c.createdAt = now;
        c.updatedAt = now;
        return c;
    }

    public static Channel createPrivate(String name, String description) {
        Channel c = new Channel();
        c.type = ChannelType.PRIVATE;
        c.name = name;
        c.description = description;
        Instant now = Instant.now();
        c.createdAt = now;
        c.updatedAt = now;
        return c;
    }


    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;

        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }

        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }


    }
}
