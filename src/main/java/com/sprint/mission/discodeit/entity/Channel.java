package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    private final UUID id;
    private String name;
    private String description;
    private final ChannelType type;
    private final List<UUID> participantUserIds;
    private final Instant createdAt;
    private Instant updatedAt;

    public static Channel publicChannel(String name, String description) {
        return new Channel(name, description, ChannelType.PUBLIC, new ArrayList<>());
    }

    public static Channel privateChannel(List<UUID> participantUserIds) {
        return new Channel(null, null, ChannelType.PRIVATE, participantUserIds);
    }

    private Channel(String name, String description, ChannelType type, List<UUID> participantUserIds) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.type = type;
        this.participantUserIds = participantUserIds == null ? new ArrayList<>() : new ArrayList<>(participantUserIds);
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void update(String name, String description) {
        if (name != null) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        this.updatedAt = Instant.now();
    }
}
