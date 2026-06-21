package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.SseBinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.event.SseChannelChangedEvent;
import com.sprint.mission.discodeit.event.SseNotificationCreatedEvent;
import com.sprint.mission.discodeit.event.SseUserChangedEvent;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(
    prefix = "discodeit.realtime.kafka",
    name = "enabled",
    havingValue = "true"
)
public class RealtimeKafkaProduceRequiredEventListener {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) {
    send(KafkaEventTopics.REALTIME_MESSAGE_CREATED, event.channelId().toString(), event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(SseNotificationCreatedEvent event) {
    send(KafkaEventTopics.REALTIME_SSE_NOTIFICATION_CREATED, event.receiverId().toString(), event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(SseBinaryContentUpdatedEvent event) {
    send(KafkaEventTopics.REALTIME_SSE_BINARY_CONTENT_UPDATED,
        event.binaryContent().id().toString(), event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(SseChannelChangedEvent event) {
    send(KafkaEventTopics.REALTIME_SSE_CHANNEL_CHANGED, event.channel().id().toString(), event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(SseUserChangedEvent event) {
    send(KafkaEventTopics.REALTIME_SSE_USER_CHANGED, event.user().id().toString(), event);
  }

  private void send(String topic, String key, Object event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, payload);
      future.whenComplete((result, exception) -> {
        if (exception != null) {
          log.error("Realtime Kafka event publish failed. topic={}, key={}", topic, key, exception);
          return;
        }
        log.info("Realtime Kafka event published. topic={}, key={}", topic, key);
      });
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Realtime Kafka event serialization failed. topic=" + topic, e);
    }
  }
}
