package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
    private static final long serialVersionUID= 1L;
    private final UUID id;
    private final UUID channelId;
    private final UUID userId;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant lastedAt;


    public ReadStatus(UUID userId,UUID channelId){
        this.id=UUID.randomUUID();
        this.channelId=channelId;
        this.userId = userId;
        this.createdAt = Instant.now();
        this.updatedAt = null;
        this.lastedAt=Instant.now();



    }
}
