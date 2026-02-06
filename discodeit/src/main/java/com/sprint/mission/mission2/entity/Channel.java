package com.sprint.mission.mission2.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String name;
    private UUID ownerId;

    public Channel(UUID id, String name, UUID ownerId) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
    }

    public void update(String name) {
        this.updatedAt = System.currentTimeMillis();
        this.name = name;
    }
}