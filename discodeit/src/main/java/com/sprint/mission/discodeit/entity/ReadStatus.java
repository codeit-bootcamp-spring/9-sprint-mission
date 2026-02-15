package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@ToString(callSuper = true)
@Getter
public class ReadStatus extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private final UUID userId;
    private final UUID channelId;

    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId){
        super();
        this.userId = userId;
        this.channelId = channelId;

        System.out.println("ReadStatus 생성 - " + this.toString());
    }

    public void updateLastReadAt(Instant time){
        this.lastReadAt = time;
    }
}
