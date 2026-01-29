package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class BaseEntity implements Serializable {
    protected static final long serialVersionUID = 1L;
    protected final UUID id;
    protected final Instant createdAt;
    protected Instant updatedAt;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = this.updatedAt = Instant.ofEpochSecond(System.currentTimeMillis());
    }

    protected void updateUpdateAt(){
        this.updatedAt = Instant.ofEpochSecond(System.currentTimeMillis());
    }
}
