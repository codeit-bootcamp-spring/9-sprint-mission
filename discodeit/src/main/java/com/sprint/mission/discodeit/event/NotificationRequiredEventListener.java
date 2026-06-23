package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.sse.SseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final NotificationRepository notificationRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final CacheManager cacheManager;
  private final SseService sseService;
  private final ObjectMapper objectMapper;
  private final NotificationMapper notificationMapper;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(MessageCreatedEvent event) {
    var message = event.getMessage();
    var channel = message.getChannel();
    var author = message.getAuthor();

    String title = author.getUsername() + " (#" + channel.getName() + ")";
    String content = message.getContent();

    readStatusRepository.findAllByChannelId(channel.getId()).stream()
        .filter(rs -> rs.isNotificationEnabled())
        .filter(rs -> !rs.getUser().getId().equals(author.getId()))
        .forEach(rs -> {
          // 알림 생성
          Notification notification = new Notification(rs.getUser(), title, content);
          notificationRepository.save(notification);

          // 캐시 무효화
          Cache cache = cacheManager.getCache("userNotifications");
          if (cache != null) {
            cache.evict(rs.getUser().getId());
          }
        });
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(RoleUpdatedEvent event) {
    User user = userRepository.findById(event.getUserId())
        .orElseThrow();

    String title = "권한이 변경되었습니다.";
    String content = event.getOldRole().name() + " -> " + event.getNewRole().name();

    Notification notification = new Notification(user, title, content);
    notificationRepository.save(notification);

    Cache cache = cacheManager.getCache("userNotifications");
    if (cache != null) {
      cache.evict(event.getUserId());
    }
  }

  @Transactional
  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
      var message = event.getMessage();
      var channel = message.getChannel();
      var author = message.getAuthor();

      String title = author.getUsername() + " (#" + channel.getName() + ")";
      String content = message.getContent();

      readStatusRepository.findAllByChannelId(channel.getId()).stream()
          .filter(rs -> rs.isNotificationEnabled())
          .filter(rs -> !rs.getUser().getId().equals(author.getId()))
          .forEach(rs -> {
            Notification notification = new Notification(rs.getUser(), title, content);
            notificationRepository.save(notification);

            // SSE 이벤트 발송 추가
            NotificationDto notificationDto = notificationMapper.toDto(notification);
            sseService.send(List.of(rs.getUser().getId()), "notifications.created", notificationDto);
          });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}