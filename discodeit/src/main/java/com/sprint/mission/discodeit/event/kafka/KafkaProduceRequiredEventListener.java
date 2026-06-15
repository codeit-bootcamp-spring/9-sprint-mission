package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.event.SseBroadcastRequiredEvent;
import com.sprint.mission.discodeit.event.SseSendRequiredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaProduceRequiredEventListener {

  public static final String MESSAGE_CREATED_TOPIC = "discodeit.MessageCreatedEvent";
  public static final String ROLE_UPDATED_TOPIC = "discodeit.RoleUpdatedEvent";
  public static final String S3_UPLOAD_FAILED_TOPIC = "discodeit.S3UploadFailedEvent";
  public static final String SSE_SEND_REQUIRED_TOPIC = "discodeit.SseSendRequiredEvent";
  public static final String SSE_BROADCAST_REQUIRED_TOPIC = "discodeit.SseBroadcastRequiredEvent";

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    send(MESSAGE_CREATED_TOPIC, event.messageId().toString(), event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    send(ROLE_UPDATED_TOPIC, event.userId().toString(), event);
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(S3UploadFailedEvent event) {
    send(S3_UPLOAD_FAILED_TOPIC, event.binaryContentId().toString(), event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(SseSendRequiredEvent event) {
    send(SSE_SEND_REQUIRED_TOPIC, event.eventName(), event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(SseBroadcastRequiredEvent event) {
    send(SSE_BROADCAST_REQUIRED_TOPIC, event.eventName(), event);
  }

  private void send(String topic, String key, Object event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send(topic, key, payload);
      log.debug("Kafka event published: topic={}, key={}", topic, key);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize Kafka event", e);
    }
  }
}
