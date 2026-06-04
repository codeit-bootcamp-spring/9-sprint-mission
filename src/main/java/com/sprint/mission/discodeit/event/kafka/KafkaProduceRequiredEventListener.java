package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@Profile("kafka")  // kafka 프로필일 때만 활성화
@RequiredArgsConstructor
public class KafkaProduceRequiredEventListener {

  private static final String MESSAGE_TOPIC = "notification.message-created";
  private static final String ROLE_TOPIC    = "notification.role-updated";

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send(MESSAGE_TOPIC, event.channelId().toString(), payload);
      log.info("Kafka 발행 완료: topic={}, messageId={}", MESSAGE_TOPIC, event.messageId());
    } catch (Exception e) {
      log.error("Kafka 발행 실패: topic={}, messageId={}", MESSAGE_TOPIC, event.messageId(), e);
    }
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send(ROLE_TOPIC, event.userId().toString(), payload);
      log.info("Kafka 발행 완료: topic={}, userId={}", ROLE_TOPIC, event.userId());
    } catch (Exception e) {
      log.error("Kafka 발행 실패: topic={}, userId={}", ROLE_TOPIC, event.userId(), e);
    }
  }
}
