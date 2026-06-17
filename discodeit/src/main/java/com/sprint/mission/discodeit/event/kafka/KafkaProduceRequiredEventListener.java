package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
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
    try {
      // 1. 이벤트 객체를 JSON 문자열로 변환
      String payload = objectMapper.writeValueAsString(event);
      // 2. 카프카 토픽으로 메시지 전송
      kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
      log.info("Kafka Produce Success: discodeit.MessageCreatedEvent -> {}", payload);
    } catch (JsonProcessingException e) {
      log.error("Kafka Produce Error (MessageCreatedEvent)", e);
    }
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
      log.info("Kafka Produce Success: discodeit.RoleUpdatedEvent -> {}", payload);
    } catch (JsonProcessingException e) {
      log.error("Kafka Produce Error (RoleUpdatedEvent)", e);
    }
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(BinaryContentCreatedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.BinaryContentCreatedEvent", payload);
      log.info("Kafka Produce Success: discodeit.BinaryContentCreatedEvent -> {}", payload);
    } catch (JsonProcessingException e) {
      log.error("Kafka Produce Error (BinaryContentCreatedEvent)", e);
    }
  }
}
