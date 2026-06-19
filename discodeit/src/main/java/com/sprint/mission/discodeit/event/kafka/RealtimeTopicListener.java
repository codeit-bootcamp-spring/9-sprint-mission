package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.SseBinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.event.SseChannelChangedEvent;
import com.sprint.mission.discodeit.event.SseNotificationCreatedEvent;
import com.sprint.mission.discodeit.event.SseUserChangedEvent;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.sse.SseEventNames;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(
    prefix = "discodeit.realtime.kafka",
    name = "enabled",
    havingValue = "true"
)
public class RealtimeTopicListener {

  private static final String MESSAGE_DESTINATION_FORMAT = "/sub/channels.%s.messages";

  private final SimpMessagingTemplate messagingTemplate;
  private final SseService sseService;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = KafkaEventTopics.REALTIME_MESSAGE_CREATED,
      groupId = "#{@realtimeKafkaConsumerGroupId}"
  )
  public void onMessageCreatedEvent(String kafkaEvent) {
    MessageCreatedEvent event = readEvent(kafkaEvent, MessageCreatedEvent.class);
    String destination = MESSAGE_DESTINATION_FORMAT.formatted(event.channelId());
    messagingTemplate.convertAndSend(destination, event);
    log.debug("Realtime message event sent over WebSocket. destination={}, messageId={}",
        destination, event.messageId());
  }

  @KafkaListener(
      topics = KafkaEventTopics.REALTIME_SSE_NOTIFICATION_CREATED,
      groupId = "#{@realtimeKafkaConsumerGroupId}"
  )
  public void onSseNotificationCreatedEvent(String kafkaEvent) {
    SseNotificationCreatedEvent event = readEvent(kafkaEvent, SseNotificationCreatedEvent.class);
    sseService.send(
        List.of(event.receiverId()),
        SseEventNames.NOTIFICATIONS_CREATED,
        event.notification()
    );
  }

  @KafkaListener(
      topics = KafkaEventTopics.REALTIME_SSE_BINARY_CONTENT_UPDATED,
      groupId = "#{@realtimeKafkaConsumerGroupId}"
  )
  public void onSseBinaryContentUpdatedEvent(String kafkaEvent) {
    SseBinaryContentUpdatedEvent event = readEvent(kafkaEvent, SseBinaryContentUpdatedEvent.class);
    sseService.broadcast(SseEventNames.BINARY_CONTENTS_UPDATED, event.binaryContent());
  }

  @KafkaListener(
      topics = KafkaEventTopics.REALTIME_SSE_CHANNEL_CHANGED,
      groupId = "#{@realtimeKafkaConsumerGroupId}"
  )
  public void onSseChannelChangedEvent(String kafkaEvent) {
    SseChannelChangedEvent event = readEvent(kafkaEvent, SseChannelChangedEvent.class);
    if (event.isBroadcast()) {
      sseService.broadcast(event.eventName(), event.channel());
      return;
    }
    sseService.send(event.receiverIds(), event.eventName(), event.channel());
  }

  @KafkaListener(
      topics = KafkaEventTopics.REALTIME_SSE_USER_CHANGED,
      groupId = "#{@realtimeKafkaConsumerGroupId}"
  )
  public void onSseUserChangedEvent(String kafkaEvent) {
    SseUserChangedEvent event = readEvent(kafkaEvent, SseUserChangedEvent.class);
    sseService.broadcast(event.eventName(), event.user());
  }

  private <T> T readEvent(String kafkaEvent, Class<T> eventType) {
    try {
      return objectMapper.readValue(kafkaEvent, eventType);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(
          "Realtime Kafka event deserialization failed. eventType=" + eventType.getSimpleName(),
          e
      );
    }
  }
}
