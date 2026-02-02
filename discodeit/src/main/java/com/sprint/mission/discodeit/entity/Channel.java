package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class Channel {
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private String name;
    private String description;
    private boolean isPrivate;

    public Channel(String name, String description, boolean isPrivate) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.name = name;
        this.description = description;
        this.isPrivate = isPrivate;
    }

    public void updateName(String name) { this.name = name; this.updatedAt = Instant.now(); }
    public void updateDescription(String description) { this.description = description; this.updatedAt = Instant.now(); }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public boolean isPrivate() { return isPrivate; }
}
