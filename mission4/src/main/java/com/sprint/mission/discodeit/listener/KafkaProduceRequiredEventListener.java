package com.sprint.mission.discodeit.listener;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.entity.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.entity.event.S3UploadFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProduceRequiredEventListener {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public static final String MESSAGE_CREATED = "discodeit.MessageCreatedEvent";
  public static final String ROLE_UPDATED = "discodeit.RoleUpdatedEvent";
  public static final String S3_UPLOAD_FAILED = "discodeit.S3UploadFailedEvent";

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) {
    sendToKafka(MESSAGE_CREATED, event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) {
    sendToKafka(ROLE_UPDATED, event);
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(S3UploadFailedEvent event) {
    sendToKafka(S3_UPLOAD_FAILED, event);

  }

  private void sendToKafka(String topic, Object event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send(topic, payload);
      log.info("Kafka 메시지 발행 완료:{} -> {}", topic, payload);

    } catch (Exception e) {
      log.error("Kafka 메시지 발행 실패: {} -> {}", topic, event, e);
    }
  }

}
