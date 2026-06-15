package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.SseBroadcastRequiredEvent;
import com.sprint.mission.discodeit.event.SseSendRequiredEvent;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.sse.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class RealtimeTopicListener {

  private final ObjectMapper objectMapper;
  private final MessageService messageService;
  private final SimpMessagingTemplate messagingTemplate;
  private final SseService sseService;

  @KafkaListener(
      topics = KafkaProduceRequiredEventListener.MESSAGE_CREATED_TOPIC,
      groupId = "#{@realtimeKafkaGroupId}"
  )
  public void onMessageCreatedEvent(String kafkaEvent) {
    MessageCreatedEvent event = read(kafkaEvent, MessageCreatedEvent.class);
    MessageDto message = messageService.find(event.messageId());
    messagingTemplate.convertAndSend("/sub/channels." + event.channelId() + ".messages", message);
  }

  @KafkaListener(
      topics = KafkaProduceRequiredEventListener.SSE_SEND_REQUIRED_TOPIC,
      groupId = "#{@realtimeKafkaGroupId}"
  )
  public void onSseSendRequiredEvent(String kafkaEvent) {
    SseSendRequiredEvent event = read(kafkaEvent, SseSendRequiredEvent.class);
    sseService.send(event.receiverIds(), event.eventName(), event.data());
  }

  @KafkaListener(
      topics = KafkaProduceRequiredEventListener.SSE_BROADCAST_REQUIRED_TOPIC,
      groupId = "#{@realtimeKafkaGroupId}"
  )
  public void onSseBroadcastRequiredEvent(String kafkaEvent) {
    SseBroadcastRequiredEvent event = read(kafkaEvent, SseBroadcastRequiredEvent.class);
    sseService.broadcast(event.eventName(), event.data());
  }

  private <T> T read(String kafkaEvent, Class<T> eventType) {
    try {
      return objectMapper.readValue(kafkaEvent, eventType);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to deserialize Kafka event", e);
    }
  }
}
