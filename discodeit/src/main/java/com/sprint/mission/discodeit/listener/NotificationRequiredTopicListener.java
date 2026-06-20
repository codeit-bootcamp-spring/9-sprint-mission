package com.sprint.mission.discodeit.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;
  private final SseService sseService;

  @KafkaListener(
      topics = "discodeit.MessageCreatedEvent",
      groupId = "discodeit-notification-group"
  )
  public void onMessageCreatedEvent(String kafkaEvent) {
    log.info("Kafka Topic 수신 -> 알림 서비스 소비 시작 (MessageCreatedEvent)");
    log.debug("수신된 페이로드 데이터: {}", kafkaEvent);

    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

      UUID notificationId = UUID.randomUUID();
      UUID receiverId = event.authorId();

      NotificationDto notificationDto = new NotificationDto(
          notificationId,
          Instant.now(),
          receiverId,
          "새로운 메시지 알림",
          event.content()
      );

      String eventName = "notifications.created";
      List<UUID> receiverIds = List.of(receiverId);

      sseService.send(receiverIds, eventName, notificationDto);

      log.info("MessageCreatedEvent 기반 알림 생성 및 실시간 SSE 발송 성공 완료!");
    } catch (JsonProcessingException e) {
      log.error("MessageCreatedEvent 카프카 메시지 역직렬화(Parsing) 실패", e);
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(
      topics = "discodeit.RoleUpdatedEvent",
      groupId = "discodeit-notification-group"
  )
  public void onRoleUpdatedEvent(String kafkaEvent) {
    log.info("Kafka Topic 수신 -> 알림 서비스 소비 시작 (RoleUpdatedEvent)");
    log.debug("수신된 페이로드 데이터: {}", kafkaEvent);

    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

      UUID notificationId = UUID.randomUUID();
      UUID receiverId = event.userId();

      NotificationDto notificationDto = new NotificationDto(
          notificationId,
          Instant.now(),
          receiverId,
          "역할 변경 알림",
          "서버 내 권한/역할 정책이 변경되었습니다."
      );

      sseService.send(List.of(receiverId), "notifications.created", notificationDto);

      log.info("RoleUpdatedEvent 기반 알림 생성 및 발송 성공 완료!");
    } catch (JsonProcessingException e) {
      log.error("RoleUpdatedEvent 카프카 메시지 역직렬화(Parsing) 실패", e);
      throw new RuntimeException(e);
    }
  }
}