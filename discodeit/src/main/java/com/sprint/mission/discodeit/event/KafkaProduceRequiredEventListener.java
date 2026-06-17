package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.kafka.enabled", havingValue = "true")
public class KafkaProduceRequiredEventListener {

  private static final String TOPIC_MESSAGE_CREATED = "discodeit.MessageCreatedEvent";
  private static final String TOPIC_ROLE_UPDATED = "discodeit.RoleUpdatedEvent";
  private static final String TOPIC_S3_UPLOAD_FAILED = "discodeit.S3UploadFailedEvent";

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Async
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    send(TOPIC_MESSAGE_CREATED, event);
  }

  @Async
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    send(TOPIC_ROLE_UPDATED, event);
  }

  @Async
  @EventListener
  public void on(S3UploadFailedEvent event) {
    send(TOPIC_S3_UPLOAD_FAILED, event);
  }

  private void send(String topic, Object payload) {
    try {
      String message = objectMapper.writeValueAsString(payload);
      kafkaTemplate.send(topic, message);
      log.info("Kafka 메시지 전송: topic={}", topic);
    } catch (JsonProcessingException e) {
      log.error("Kafka 메시지 직렬화 실패: topic={}", topic, e);
    }
  }
}
