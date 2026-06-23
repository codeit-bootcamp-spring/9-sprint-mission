package com.sprint.mission.discodeit.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.event.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.event.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  @Async("eventExecutor")
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) throws JsonProcessingException {
    send("discodeit.MessageCreatedEvent", event);
  }

  @Async("eventExecutor")
  @TransactionalEventListener
  public void on(NotificationCreatedEvent event) throws JsonProcessingException {
    send("discodeit.NotificationCreatedEvent", event);
  }

  @Async("eventExecutor")
  @TransactionalEventListener
  public void on(BinaryContentUpdatedEvent event) throws JsonProcessingException {
    send("discodeit.BinaryContentUpdatedEvent", event);
  }

  @Async("eventExecutor")
  @TransactionalEventListener
  public void on(ChannelUpdatedEvent event) throws JsonProcessingException {
    send("discodeit.ChannelUpdatedEvent", event);
  }

  @Async("eventExecutor")
  @TransactionalEventListener
  public void on(UserUpdatedEvent event) throws JsonProcessingException {
    send("discodeit.UserUpdatedEvent", event);
  }

  @Async("eventExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) throws JsonProcessingException {
    send("discodeit.RoleUpdatedEvent", event);
  }

  @Async("eventExecutor")
  @TransactionalEventListener
  public void on(S3UploadFailedEvent event) throws JsonProcessingException {
    send("discodeit.S3UploadFailedEvent", event);
  }

  private void send(String topic, Object event) throws JsonProcessingException {
    kafkaTemplate.send(topic, objectMapper.writeValueAsString(event));
  }
}

