package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;
  private final NotificationService notificationService;
  private final ReadStatusRepository readStatusRepository;
  private final SimpMessagingTemplate messagingTemplate;

  @KafkaListener(
      topics = "discodeit.MessageCreatedEvent",
      groupId = "#{T(java.util.UUID).randomUUID().toString()}"
  )
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
      MessageDto message = event.message();

      // 웹소켓으로 메시지 전송 (모든 서버 인스턴스가 각자 실행)
      String destination = "/sub/channels." + message.channelId() + ".messages";
      messagingTemplate.convertAndSend(destination, message);
      log.info("웹소켓 메시지 전송 완료 - destination: {}", destination);

      // 알림 생성
      String title = message.author().username() + " (#" + message.channelId() + ")";
      String content = message.content();

      List<ReadStatus> notifyTargets = readStatusRepository
          .findAllByChannel_Id(message.channelId())
          .stream()
          .filter(ReadStatus::isNotificationEnabled)
          .filter(rs -> !rs.getUser().getId().equals(message.author().id()))
          .toList();

      notifyTargets.forEach(rs -> {
        User receiver = rs.getUser();
        notificationService.create(receiver, title, content);
        log.info("알림 생성 - 수신자: {}", receiver.getUsername());
      });

    } catch (JsonProcessingException e) {
      log.error("MessageCreatedEvent 처리 실패", e);
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      log.info("역할 변경 이벤트 수신: {}", kafkaEvent);
    } catch (Exception e) {
      log.error("RoleUpdatedEvent 처리 실패", e);
      throw new RuntimeException(e);
    }
  }
}