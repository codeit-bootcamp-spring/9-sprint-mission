package com.sprint.mission.discodeit.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "discodeit.MessageCreatedEvent",
      groupId = "discodeit-notification-group"
  )
  public void onMessageCreatedEvent(String kafkaEvent) {
    log.info("Kafka Topic 수신 -> 알림 서비스 소비 시작 (MessageCreatedEvent)");
    log.debug("수신된 페이로드 데이터: {}", kafkaEvent);

    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

      log.info("MessageCreatedEvent 기반 알림 생성 및 발송 성공 완료!");
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

      log.info("RoleUpdatedEvent 기반 알림 생성 및 발송 성공 완료!");
    } catch (JsonProcessingException e) {
      log.error("RoleUpdatedEvent 카프카 메시지 역직렬화(Parsing) 실패", e);
      throw new RuntimeException(e);
    }
  }
}