package com.sprint.mission.discodeit.error;

public record ErrorResponse(
        int status,
        String message,
        String timestamp
) {
}
