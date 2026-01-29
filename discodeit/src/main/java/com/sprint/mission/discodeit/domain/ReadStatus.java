package com.sprint.mission.discodeit.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReadStatus {
    private UUID id;
    private UUID userId;
    private UUID channelId;
    private Instant readAt;
    private Instant createdAt;
    private Instant updatedAt;

    // [추가됨] 시간을 업데이트하는 메서드
    public void update(Instant readAt) {
        this.readAt = readAt;
        this.updatedAt = Instant.now();
    }
}