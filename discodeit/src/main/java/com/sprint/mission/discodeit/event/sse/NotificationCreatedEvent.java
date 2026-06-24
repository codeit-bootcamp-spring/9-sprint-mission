package com.sprint.mission.discodeit.event.sse;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationCreatedEvent {

  private final List<NotificationDto> notifications;
}
