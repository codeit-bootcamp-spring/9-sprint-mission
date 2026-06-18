package com.sprint.mission.discodeit.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.listener.KafkaProduceRequiredEventListener;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketConsumer {

  private final SimpMessagingTemplate template;
  private final BasicMessageService messageService;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = KafkaProduceRequiredEventListener.MESSAGE_CREATED,
      groupId = "#{T(java.util.UUID).randomUUID().toString()}")
  public void consume(String payload) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(payload, MessageCreatedEvent.class);
      MessageDto dto = messageService.find(event.messageId());
      template.convertAndSend("/sub/channels." + event.channelId() + ".messages", dto);
    } catch (Exception e) {
      log.error("Kafka 메시지 수신 후 웹소켓 전송 실패: {}", payload, e);
    }

  }

}
