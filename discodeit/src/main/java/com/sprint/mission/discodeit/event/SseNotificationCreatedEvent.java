package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.response.NotificationDto;
import java.util.UUID;

public record SseNotificationCreatedEvent(
    UUID receiverId,
    NotificationDto notification
) {

}
