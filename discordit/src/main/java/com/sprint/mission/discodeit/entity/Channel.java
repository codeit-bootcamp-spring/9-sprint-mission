package com.sprint.mission.discodeit.entity;

import java.util.List;
import java.io.Serializable;
import java.util.UUID;


public class Channel implements Serializable {
      private static final long serialVersionUID = 1L;

    private final UUID id;
    private String displayName;
    private Long createdAt;
    private Long updatedAt;
    private ChannelType type;


    public Channel(String displayName,ChannelType type) {
        this.id = UUID.randomUUID();
        this.displayName = displayName;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
        this.type = type;

    }
    public void update(String displayName) {
        this.displayName = displayName;
    }

    public UUID getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayname(String displayName) {
        this.displayName = displayName;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

