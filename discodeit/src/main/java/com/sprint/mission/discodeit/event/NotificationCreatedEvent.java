package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import java.util.List;

public record NotificationCreatedEvent(
    List<NotificationDto> notificationDtos
) {

}
