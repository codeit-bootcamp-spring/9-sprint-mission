package com.sprint.mission.discodeit.entity.status;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.rmi.server.UID;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID userId;
    private final UUID channelId;
    private final UUID messageId;
    private final Long createdAt;
    private Long updatedAt;
    private UUID lastReadMessageId;


    @Builder
    public ReadStatus(UUID userId, UUID channelId, UUID messageId) {
        this.userId = userId;
        this.channelId = channelId;
        this.messageId = messageId;
        this.createdAt = Instant.now().toEpochMilli();
        this.updatedAt = this.createdAt;

    }
    //사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델

    public void read(UUID messageId){
        if (isAlreadyRead(messageId)){
            return;
        }
        this.lastReadMessageId = messageId;
        this.updatedAt = Instant.now().toEpochMilli();
    }

    private boolean isAlreadyRead(UUID messageId){
        if (lastReadMessageId == null){
            return false;
        }
        return lastReadMessageId.equals(messageId);
    }

    public static ReadStatus createForChannel (UUID userId,UUID channelId){
        return new ReadStatus(userId,channelId,null);
    }

}
