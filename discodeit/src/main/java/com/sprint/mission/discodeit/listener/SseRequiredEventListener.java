package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.event.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.event.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.event.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.UserUpdatedEvent;
import com.sprint.mission.discodeit.service.basic.SseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

//@Component
@RequiredArgsConstructor
public class SseRequiredEventListener {

  private final SseService sseService;

  @EventListener
  public void handleNotificationCreated(NotificationCreatedEvent event) {
    event.notificationDtos().forEach(dto ->
        sseService.send(
            List.of(dto.receiverId()),
            "notifications.created",
            dto
        )
    );
  }

  @EventListener
  public void handleBinaryContentUpdated(BinaryContentUpdatedEvent event) {
    sseService.broadcast(
        "binaryContents.updated",
        event.binaryContentDto()
    );
  }

  @EventListener
  public void handleChannelUpdated(ChannelUpdatedEvent event) {
    String eventName = switch (event.updatedType()) {
      case "created" -> "channels.created";
      case "updated" -> "channels.updated";
      case "deleted" -> "channels.deleted";
      default -> throw new IllegalArgumentException("Unknown event type: " + event.updatedType());
    };
    sseService.broadcast(eventName, event.channelDto());
  }

  @EventListener
  public void handleUserUpdated(UserUpdatedEvent event) {
    String eventName = switch (event.updatedType()) {
      case "created" -> "users.created";
      case "updated" -> "users.updated";
      case "deleted" -> "users.deleted";
      default -> throw new IllegalArgumentException("Unknown event type: " + event.updatedType());
    };
    sseService.broadcast(eventName, event.userDto());
  }
}
