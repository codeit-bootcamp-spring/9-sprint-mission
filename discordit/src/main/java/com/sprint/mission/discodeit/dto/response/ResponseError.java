package com.sprint.mission.discodeit.dto.response;

public record ResponseError(
        int status,
        String error,
        String message
) {
}