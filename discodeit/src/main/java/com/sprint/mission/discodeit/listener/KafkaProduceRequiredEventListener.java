package com.sprint.mission.discodeit.listener; // 📁 스크린샷의 listener 패키지 경로로 지정

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProduceRequiredEventListener {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    log.debug("Spring Event 수신 -> Kafka 발행 시작 (MessageCreatedEvent): {}", event);
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
      log.info("Kafka 토픽 전송 완료: discodeit.MessageCreatedEvent");
    } catch (JsonProcessingException e) {
      log.error("MessageCreatedEvent JSON 직렬화 실패", e);
    }
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    log.debug("Spring Event 수신 -> Kafka 발행 시작 (RoleUpdatedEvent): {}", event);
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
      log.info("Kafka 토픽 전송 완료: discodeit.RoleUpdatedEvent");
    } catch (JsonProcessingException e) {
      log.error("RoleUpdatedEvent JSON 직렬화 실패", e);
    }
  }
}