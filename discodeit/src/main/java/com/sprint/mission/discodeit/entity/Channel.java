package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Channel {
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private String name;
    private String description;
    private ChannelType type;

    public Channel(ChannelType type, String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public void update(String name, String description) {
        if (isPrivate()) {
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        if (name != null) this.name = name;
        if (description != null) this.description = description;
        this.updatedAt = Instant.now();
    }

    public boolean isPrivate() {
        return this.type == ChannelType.PRIVATE;
    }
}

