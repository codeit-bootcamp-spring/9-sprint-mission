package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@ToString
@Getter
public abstract class BaseEntity implements Serializable{
    protected final UUID id;

    @ToString.Exclude
    protected final Instant createdAt;

    @ToString.Exclude
    protected Instant updatedAt;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = this.updatedAt = Instant.ofEpochSecond(System.currentTimeMillis());
    }

    protected void updateUpdateAt(){
        this.updatedAt = Instant.ofEpochSecond(System.currentTimeMillis());
    }
}
