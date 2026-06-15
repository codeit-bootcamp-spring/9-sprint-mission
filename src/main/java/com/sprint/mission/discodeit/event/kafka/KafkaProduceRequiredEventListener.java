package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageCreatedEvent event) {
        log.info("MessageCreatedEvent 수신: {}", event);
        publishToKafka("discodeit.MessageCreatedEvent", event);
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleUpdatedEvent event) {
        publishToKafka("discodeit.RoleUpdatedEvent", event);
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(S3UploadFailedEvent event) {
        publishToKafka("discodeit.S3UploadFailedEvent", event);
    }

    private void publishToKafka(String topic, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event: {}", event, e);
        }
    }
}