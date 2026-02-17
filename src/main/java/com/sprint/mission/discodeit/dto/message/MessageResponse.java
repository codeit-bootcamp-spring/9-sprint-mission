package com.sprint.mission.discodeit.dto.message;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class MessageResponse {
    private UUID id;
    private String content;
    private UUID userId;
    private UUID channelId;
    private Instant createdAt;
    private Instant updatedAt;
    private List<UUID> attachmentIds;
}
