package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {
    private UUID id;
    private UUID channelId;
    private UUID userId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastedAt;


    public ReadStatus(User user,Channel channel){
        this.id=UUID.randomUUID();
        this.channelId=channel.getId();
        this.userId = user.getId();
        this.createdAt = Instant.now();
        this.updatedAt = null;
        this.lastedAt=Instant.now();



    }
}
