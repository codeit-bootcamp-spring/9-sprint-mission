package com.sprint.mission.discodeit.event;

public record AdminErrorAlertEvent(
        String errorType,
        String errorMessage
) {
}