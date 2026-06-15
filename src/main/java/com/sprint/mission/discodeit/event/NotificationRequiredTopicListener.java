package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.NotificationType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {

  private static final String MESSAGE_TOPIC = "notification.message-created";
  private static final String ROLE_TOPIC    = "notification.role-updated";

  private final ObjectMapper objectMapper;
  private final NotificationRepository notificationRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;

  @Transactional
  @KafkaListener(topics = MESSAGE_TOPIC, groupId = "discodeit-group")
  public void onMessageCreated(String payload) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(payload, MessageCreatedEvent.class);

      log.debug("Kafka 수신: topic={}, messageId={}", MESSAGE_TOPIC, event.messageId());

      List<Notification> notifications = readStatusRepository
          .findAllByChannelIdWithUser(event.channelId())
          .stream()
          .filter(rs -> rs.isNotificationEnabled())
          .filter(rs -> !rs.getUser().getId().equals(event.authorId())) // 발신자 제외
          .map(rs -> new Notification(
              rs.getUser(),
              NotificationType.MESSAGE_CREATED,
              String.format("[%s] 새로운 메시지가 도착했습니다.", event.channelName()),
              event.messageId()
          ))
          .toList();

      notificationRepository.saveAll(notifications);

      log.info("메시지 알림 저장 완료: channelId={}, 알림 수={}", event.channelId(), notifications.size());

    } catch (Exception e) {
      log.error("Kafka 메시지 처리 실패: topic={}", MESSAGE_TOPIC, e);
    }
  }

  @Transactional
  @KafkaListener(topics = ROLE_TOPIC, groupId = "discodeit-group")
  public void onRoleUpdated(String payload) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(payload, RoleUpdatedEvent.class);

      log.debug("Kafka 수신: topic={}, userId={}", ROLE_TOPIC, event.userId());

      User user = userRepository.findById(event.userId()).orElse(null);
      if (user == null) {
        log.warn("권한 변경 알림 대상 유저 없음: userId={}", event.userId());
        return;
      }

      Notification notification = new Notification(
          user,
          NotificationType.ROLE_UPDATED,
          String.format("권한이 %s에서 %s로 변경되었습니다.", event.oldRole(), event.newRole()),
          event.userId()
      );
      notificationRepository.save(notification);

      log.info("권한 변경 알림 저장 완료: userId={}", event.userId());

    } catch (Exception e) {
      log.error("Kafka 메시지 처리 실패: topic={}", ROLE_TOPIC, e);
    }
  }
}
