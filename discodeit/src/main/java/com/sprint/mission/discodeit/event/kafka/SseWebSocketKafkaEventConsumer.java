package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.ChannelEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.UserEvent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseWebSocketKafkaEventConsumer {

  private final ObjectMapper objectMapper;
  private final SseService sseService;
  private final SimpMessagingTemplate messagingTemplate;
  private final MessageService messageService;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;

  // ✅ WebSocket: 메시지 생성 → 채널 구독자에게 전송
  // 인스턴스마다 다른 group id → 3개 인스턴스 모두 수신
  @KafkaListener(
      topics = "discodeit.MessageCreatedEvent",
      groupId = "discodeit-websocket-${HOSTNAME:local}"
  )
  public void onMessageCreatedForWebSocket(String payload) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(payload, MessageCreatedEvent.class);
      String destination = "/sub/channels." + event.channelId() + ".messages";
      MessageDto messageDto = messageService.find(event.messageId());
      messagingTemplate.convertAndSend(destination, messageDto);
      log.debug("WebSocket 메시지 전송: destination={}", destination);
    } catch (Exception e) {
      log.error("WebSocket 메시지 전송 실패: {}", e.getMessage());
    }
  }

  // ✅ SSE: 파일 업로드 완료 → 전체 브로드캐스트
  @KafkaListener(
      topics = "discodeit.BinaryContentCreatedEvent",
      groupId = "discodeit-sse-${HOSTNAME:local}"
  )
  public void onBinaryContentCreated(String payload) {
    try {
      BinaryContentCreatedEvent event = objectMapper.readValue(payload,
          BinaryContentCreatedEvent.class);
      BinaryContentDto dto = binaryContentMapper.toDto(
          binaryContentRepository.findById(event.binaryContentId()).orElseThrow()
      );
      sseService.broadcast("binaryContents.updated", dto);
      log.debug("SSE BinaryContent 브로드캐스트 완료");
    } catch (Exception e) {
      log.error("SSE BinaryContent 브로드캐스트 실패: {}", e.getMessage());
    }
  }

  // ✅ SSE: 채널 생성/수정/삭제 → 전체 브로드캐스트
  @KafkaListener(
      topics = "discodeit.ChannelEvent",
      groupId = "discodeit-sse-${HOSTNAME:local}"
  )
  public void onChannelEvent(String payload) {
    try {
      ChannelEvent event = objectMapper.readValue(payload, ChannelEvent.class);
      sseService.broadcast(event.eventName(), event.channelDto());
      log.debug("SSE 채널 이벤트 브로드캐스트 완료: {}", event.eventName());
    } catch (Exception e) {
      log.error("SSE 채널 이벤트 브로드캐스트 실패: {}", e.getMessage());
    }
  }

  // ✅ SSE: 유저 생성/수정/삭제 → 전체 브로드캐스트
  @KafkaListener(
      topics = "discodeit.UserEvent",
      groupId = "discodeit-sse-${HOSTNAME:local}"
  )
  public void onUserEvent(String payload) {
    try {
      UserEvent event = objectMapper.readValue(payload, UserEvent.class);
      sseService.broadcast(event.eventName(), event.userDto());
      log.debug("SSE 유저 이벤트 브로드캐스트 완료: {}", event.eventName());
    } catch (Exception e) {
      log.error("SSE 유저 이벤트 브로드캐스트 실패: {}", e.getMessage());
    }
  }
}