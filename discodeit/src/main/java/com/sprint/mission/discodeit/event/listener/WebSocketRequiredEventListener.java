package com.sprint.mission.discodeit.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * 분산 환경에서는 메시지를 생성한 인스턴스와 구독 중인 클라이언트의 웹소켓 연결을
 * 보유한 인스턴스가 다를 수 있다. 따라서 로컬 ApplicationEvent 대신 Kafka 토픽을
 * 인스턴스별 고유 컨슈머 그룹(discodeit.kafka.instance-group-id)으로 구독해
 * 모든 인스턴스가 동일 이벤트를 전달받아 각자 보유한 로컬 세션에만 브로드캐스트한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {

  private final SimpMessagingTemplate messagingTemplate;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "discodeit.MessageCreatedEvent",
      groupId = "${discodeit.kafka.instance-group-id}",
      containerFactory = "fanoutKafkaListenerContainerFactory"
  )
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
      MessageDto messageDto = event.getData();
      messagingTemplate.convertAndSend(
          "/sub/channels." + messageDto.channelId() + ".messages",
          messageDto
      );
    } catch (JsonProcessingException e) {
      log.error("웹소켓 팬아웃 이벤트 역직렬화 실패", e);
    }
  }
}