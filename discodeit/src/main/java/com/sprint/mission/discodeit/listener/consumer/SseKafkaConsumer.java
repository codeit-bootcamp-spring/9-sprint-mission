package com.sprint.mission.discodeit.listener.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.event.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.event.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.UserUpdatedEvent;
import com.sprint.mission.discodeit.service.basic.SseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseKafkaConsumer {

  private final SseService sseService;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "discodeit.NotificationCreatedEvent",
      groupId = "discodeit-sse-group-#{T(java.util.UUID).randomUUID().toString()}"
  )
  public void onNotificationCreated(String payload) throws JsonProcessingException {
    NotificationCreatedEvent event = objectMapper.readValue(payload,
        NotificationCreatedEvent.class);
    event.notificationDtos().forEach(dto ->
        sseService.send(List.of(dto.receiverId()), "notifications.created", dto)
    );
  }

  @KafkaListener(
      topics = "discodeit.BinaryContentUpdatedEvent",
      groupId = "discodeit-sse-group-#{T(java.util.UUID).randomUUID().toString()}"
  )
  public void onBinaryContentUpdated(String payload) throws JsonProcessingException {
    BinaryContentUpdatedEvent event = objectMapper.readValue(payload,
        BinaryContentUpdatedEvent.class);
    sseService.broadcast("binaryContents.updated", event.binaryContentDto());
  }

  @KafkaListener(
      topics = "discodeit.ChannelUpdatedEvent",
      groupId = "discodeit-sse-group-#{T(java.util.UUID).randomUUID().toString()}"
  )
  public void onChannelUpdated(String payload) throws JsonProcessingException {
    ChannelUpdatedEvent event = objectMapper.readValue(payload, ChannelUpdatedEvent.class);
    String eventName = switch (event.updatedType()) {
      case "created" -> "channels.created";
      case "updated" -> "channels.updated";
      case "deleted" -> "channels.deleted";
      default -> throw new IllegalArgumentException("Unknown event type: " + event.updatedType());
    };
    sseService.broadcast(eventName, event.channelDto());
  }

  @KafkaListener(
      topics = "discodeit.UserUpdatedEvent",
      groupId = "discodeit-sse-group-#{T(java.util.UUID).randomUUID().toString()}"
  )
  public void onUserUpdated(String payload) throws JsonProcessingException {
    UserUpdatedEvent event = objectMapper.readValue(payload, UserUpdatedEvent.class);
    String eventName = switch (event.updatedType()) {
      case "created" -> "users.created";
      case "updated" -> "users.updated";
      case "deleted" -> "users.deleted";
      default -> throw new IllegalArgumentException("Unknown event type: " + event.updatedType());
    };
    sseService.broadcast(eventName, event.userDto());
  }
}