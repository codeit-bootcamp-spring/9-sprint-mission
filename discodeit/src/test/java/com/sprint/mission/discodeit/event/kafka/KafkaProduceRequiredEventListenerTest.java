package com.sprint.mission.discodeit.event.kafka;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.entity.UserRole;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@ExtendWith(MockitoExtension.class)
class KafkaProduceRequiredEventListenerTest {

  @Mock
  private KafkaTemplate<String, String> kafkaTemplate;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private KafkaProduceRequiredEventListener listener;

  @BeforeEach
  void setUp() {
    listener = new KafkaProduceRequiredEventListener(kafkaTemplate, objectMapper);
  }

  @Test
  @DisplayName("MessageCreatedEvent를 Kafka topic으로 발행한다")
  void onMessageCreatedEvent_sendsKafkaEvent() throws Exception {
    UUID messageId = UUID.randomUUID();
    MessageCreatedEvent event = new MessageCreatedEvent(
        messageId,
        UUID.randomUUID(),
        "general",
        UUID.randomUUID(),
        "author",
        "hello"
    );
    given(kafkaTemplate.send(
        eq(KafkaEventTopics.MESSAGE_CREATED),
        eq(messageId.toString()),
        eq(objectMapper.writeValueAsString(event))
    )).willReturn(CompletableFuture.completedFuture(null));

    listener.on(event);

    then(kafkaTemplate).should()
        .send(eq(KafkaEventTopics.MESSAGE_CREATED), eq(messageId.toString()),
            eq(objectMapper.writeValueAsString(event)));
  }

  @Test
  @DisplayName("RoleUpdatedEvent를 Kafka topic으로 발행한다")
  void onRoleUpdatedEvent_sendsKafkaEvent() throws Exception {
    UUID userId = UUID.randomUUID();
    RoleUpdatedEvent event = new RoleUpdatedEvent(userId, UserRole.USER, UserRole.ADMIN);
    given(kafkaTemplate.send(
        eq(KafkaEventTopics.ROLE_UPDATED),
        eq(userId.toString()),
        eq(objectMapper.writeValueAsString(event))
    )).willReturn(CompletableFuture.completedFuture(null));

    listener.on(event);

    then(kafkaTemplate).should()
        .send(eq(KafkaEventTopics.ROLE_UPDATED), eq(userId.toString()),
            eq(objectMapper.writeValueAsString(event)));
  }

  @Test
  @DisplayName("S3UploadFailedEvent를 Kafka topic으로 발행한다")
  void onS3UploadFailedEvent_sendsKafkaEvent() throws Exception {
    UUID binaryContentId = UUID.randomUUID();
    S3UploadFailedEvent event = new S3UploadFailedEvent(
        "S3 binary content upload",
        "request-123",
        binaryContentId,
        "S3 access denied"
    );
    given(kafkaTemplate.send(
        eq(KafkaEventTopics.S3_UPLOAD_FAILED),
        eq(binaryContentId.toString()),
        eq(objectMapper.writeValueAsString(event))
    )).willReturn(CompletableFuture.completedFuture(null));

    listener.on(event);

    then(kafkaTemplate).should()
        .send(eq(KafkaEventTopics.S3_UPLOAD_FAILED), eq(binaryContentId.toString()),
            eq(objectMapper.writeValueAsString(event)));
  }

  @Test
  @DisplayName("Kafka 발행 실패도 CompletableFuture 콜백에서 처리한다")
  void onMessageCreatedEvent_handlesSendFailure() throws Exception {
    UUID messageId = UUID.randomUUID();
    MessageCreatedEvent event = new MessageCreatedEvent(
        messageId,
        UUID.randomUUID(),
        "general",
        UUID.randomUUID(),
        "author",
        "hello"
    );
    CompletableFuture<SendResult<String, String>> failedFuture = new CompletableFuture<>();
    failedFuture.completeExceptionally(new RuntimeException("broker timeout"));
    given(kafkaTemplate.send(
        eq(KafkaEventTopics.MESSAGE_CREATED),
        eq(messageId.toString()),
        eq(objectMapper.writeValueAsString(event))
    )).willReturn(failedFuture);

    listener.on(event);

    then(kafkaTemplate).should()
        .send(eq(KafkaEventTopics.MESSAGE_CREATED), eq(messageId.toString()),
            eq(objectMapper.writeValueAsString(event)));
  }
}
