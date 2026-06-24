package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.event.message.UserLogInOutEvent;
import com.sprint.mission.discodeit.event.sse.BinaryContentStatusUpdatedEvent;
import com.sprint.mission.discodeit.event.sse.ChannelEvent;
import com.sprint.mission.discodeit.event.sse.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.sse.UserEvent;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * SSE 연결은 인스턴스의 로컬 메모리(SseEmitterRepository)에만 보관되므로, 이벤트가
 * 발생한 인스턴스가 아니라 연결을 들고 있는 인스턴스가 직접 push해야 한다. 모든
 * 인스턴스가 동일 이벤트를 받아야 하므로 인스턴스별 고유 컨슈머 그룹을 사용한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SseRequiredTopicListener {

  private final SseService sseService;
  private final UserService userService;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "discodeit.NotificationCreatedEvent",
      groupId = "${discodeit.kafka.instance-group-id}",
      containerFactory = "fanoutKafkaListenerContainerFactory"
  )
  public void onNotificationCreatedEvent(String kafkaEvent) {
    deserialize(kafkaEvent, NotificationCreatedEvent.class).ifPresent(event ->
        event.getNotifications().forEach(dto ->
            sseService.send(Set.of(dto.receiverId()), "notifications.created", dto)
        )
    );
  }

  @KafkaListener(
      topics = "discodeit.BinaryContentStatusUpdatedEvent",
      groupId = "${discodeit.kafka.instance-group-id}",
      containerFactory = "fanoutKafkaListenerContainerFactory"
  )
  public void onBinaryContentStatusUpdatedEvent(String kafkaEvent) {
    deserialize(kafkaEvent, BinaryContentStatusUpdatedEvent.class).ifPresent(event ->
        sseService.broadcast("binaryContents.updated", event.getBinaryContentDto())
    );
  }

  @KafkaListener(
      topics = "discodeit.ChannelEvent",
      groupId = "${discodeit.kafka.instance-group-id}",
      containerFactory = "fanoutKafkaListenerContainerFactory"
  )
  public void onChannelEvent(String kafkaEvent) {
    deserialize(kafkaEvent, ChannelEvent.class).ifPresent(event -> {
      String eventName = "channels." + event.getAction().name().toLowerCase();
      sseService.broadcast(eventName, event.getChannelDto());
    });
  }

  @KafkaListener(
      topics = "discodeit.UserEvent",
      groupId = "${discodeit.kafka.instance-group-id}",
      containerFactory = "fanoutKafkaListenerContainerFactory"
  )
  public void onUserEvent(String kafkaEvent) {
    deserialize(kafkaEvent, UserEvent.class).ifPresent(event -> {
      String eventName = "users." + event.getAction().name().toLowerCase();
      sseService.broadcast(eventName, event.getUserDto());
    });
  }

  @KafkaListener(
      topics = "discodeit.UserLogInOutEvent",
      groupId = "${discodeit.kafka.instance-group-id}",
      containerFactory = "fanoutKafkaListenerContainerFactory"
  )
  public void onUserLogInOutEvent(String kafkaEvent) {
    deserialize(kafkaEvent, UserLogInOutEvent.class).ifPresent(event -> {
      UserDto dto = userService.find(event.getUserId());
      sseService.broadcast("users.updated", dto);
    });
  }

  private <T> java.util.Optional<T> deserialize(String json, Class<T> type) {
    try {
      return java.util.Optional.of(objectMapper.readValue(json, type));
    } catch (JsonProcessingException e) {
      log.error("SSE 팬아웃 이벤트 역직렬화 실패: type={}", type.getSimpleName(), e);
      return java.util.Optional.empty();
    }
  }
}
