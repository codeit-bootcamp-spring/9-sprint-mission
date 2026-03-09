package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
public class Channel {

    @Id
    private UUID id;

    private Instant createdAt;
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private ChannelType type;

    private String name;
    private String description;

    protected Channel() {}

    public Channel(ChannelType type, String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.type = type;
        this.name = name;
        this.description = description;
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
