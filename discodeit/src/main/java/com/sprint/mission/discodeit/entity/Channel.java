package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Channel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private String name;
    private final UUID ownerId;
    private ChannelType type;
    private String description;
    private final Instant createdAt;
    private Instant updatedAt;

    public Channel(ChannelType type, String name, UUID ownerId, String description) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.ownerId = ownerId;
        this.type = type;
        this.description = description;
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void update(String description, String name) {
        boolean anyValueUpdated = false;
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            anyValueUpdated = true;
        }
        if (description != null && !description.equals(this.description)) {
            this.description = description;
            anyValueUpdated = true;
        }
        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
