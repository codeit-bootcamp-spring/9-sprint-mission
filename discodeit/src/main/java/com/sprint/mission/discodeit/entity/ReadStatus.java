package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private UUID userId;
    private UUID channelId;
    private UUID lastReadMessageId;
//유저가 해당 채널에서 마지막으로 읽은 메세지의 고유id이며 안 읽은 메세지 개수를 계산할 때 기준점

    public ReadStatus(UUID userId, UUID channelId, UUID lastReadMessageId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadMessageId = lastReadMessageId;
    }
    public void update(UUID lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
        this.updatedAt = Instant.now();
    }
}
/* 마지막으로 읽은 메시지 ID를 새로 받아서 정보를 갱신한다는 선언
기존에 저장되어 있던 읽음 지점을 버리고, 방금 전달받은 새로운 메시지 ID로 교체 현재 유저가 어디까지 읽었는지 최산 상태로 알게 됨
유저가 언제 마지막으로 채빙방 확인했는지 추적하기 위함이며 데이터가 최신임을 증명
 */