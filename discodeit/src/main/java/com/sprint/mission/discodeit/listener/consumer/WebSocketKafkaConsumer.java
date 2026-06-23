package com.sprint.mission.discodeit.listener.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketKafkaConsumer {

  private final SimpMessagingTemplate messagingTemplate;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "discodeit.MessageCreatedEvent",
      groupId = "discodeit-websocket-group-#{T(java.util.UUID).randomUUID().toString()}"
  )
  public void onMessageCreated(String payload) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(payload, MessageCreatedEvent.class);
      MessageDto messageDto = event.messageDto();
      UUID channelId = messageDto.channelId();

      messagingTemplate.convertAndSend(
          "/sub/channels." + channelId + ".messages",
          messageDto
      );
    } catch (Exception e) {
      log.error("MessageCreatedEvent 처리 실패", e);
    }
  }
}