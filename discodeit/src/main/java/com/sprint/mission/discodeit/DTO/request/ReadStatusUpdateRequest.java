package com.sprint.mission.discodeit.DTO.request;

import java.time.Instant;

public record ReadStatusUpdateRequest(
        Instant newLastReadAt
) {

}
