package com.sprint.mission.discodeit.entity;


import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {

    private UUID id;
    private UUID userId;
    private UUID channelId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastRead;


    public ReadStatus(UUID id, UUID userId, UUID channelId,Instant createdAt, Instant updatedAt, Instant lastRead) {
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastRead = lastRead;
    }

public void updateLastRead(Instant time){
        this.lastRead = time;
        this.updatedAt = time;
}

}
