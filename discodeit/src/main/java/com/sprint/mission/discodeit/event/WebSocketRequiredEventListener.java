package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.kafka.MessageCreatedPayload;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {
  private final SimpMessagingTemplate messagingTemplate;
  private final MessageService messageService;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "discodeit.MessageCreatedEvent",
      groupId = "websocket-group-#{T(java.util.UUID).randomUUID().toString()}"
  )
  public void handleMessage(String kafkaEvent) {
    try {
      MessageCreatedPayload payload = objectMapper.readValue(kafkaEvent, MessageCreatedPayload.class);
      UUID channelId = payload.channelId();
      
      if (payload.messageId() == null) {
        log.warn("Received MessageCreatedPayload with null messageId. Skipping WebSocket broadcasting. Payload: {}", payload);
        return;
      }

      MessageDto messageDto = messageService.find(payload.messageId());
      String destination = "/sub/channels." + channelId + ".messages";
      messagingTemplate.convertAndSend(destination, messageDto);
      log.info("Successfully broadcasted message to WebSocket subscribers for channel: {}", channelId);
    } catch (Exception e) {
      log.error("Failed to process message from Kafka for WebSocket broadcasting", e);
    }
  }
}
