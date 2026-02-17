package com.sprint.mission.discodeit.dto.readstatus;

import lombok.Getter;
import lombok.Builder;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class ReadStatusResponse {
    private UUID id;
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;
    private Instant createdAt;
    private Instant updatedAt;
}