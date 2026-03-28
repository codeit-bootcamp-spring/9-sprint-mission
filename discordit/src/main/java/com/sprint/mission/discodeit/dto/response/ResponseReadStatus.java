package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ResponseReadStatus(
    UUID id, UUID userId, UUID channelId, Instant createAt, Instant updateAt, Instant lastReadAt
) {

}
