package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    protected UUID id;
    protected Instant createdAt;
    protected Instant updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void recordUpdate() {
        this.updatedAt = Instant.now();
    }
}