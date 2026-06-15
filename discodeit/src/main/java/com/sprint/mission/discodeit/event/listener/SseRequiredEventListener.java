package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.ChannelEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.UserEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseRequiredEventListener {

  private final SseService sseService;
  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;
  private final ReadStatusRepository readStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final CacheManager cacheManager;
  private final UserRepository userRepository;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onMessageCreated(MessageCreatedEvent event) {
    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdWithUser(
        event.channelId());

    readStatuses.stream()
        .filter(rs -> !rs.getUser().getId().equals(event.authorId()))
        .forEach(rs -> {
          Notification notification = new Notification(
              rs.getUser(),
              event.authorName() + " (#" + event.channelName() + ")",
              event.content()
          );
          notificationRepository.save(notification);
          cacheManager.getCache("notifications").evict(rs.getUser().getId());

          NotificationDto notificationDto = notificationMapper.toDto(notification);
          sseService.send(
              List.of(rs.getUser().getId()),
              "notifications.created",
              notificationDto
          );
        });
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onRoleUpdated(RoleUpdatedEvent event) {
    User user = userRepository.findById(event.userId())
        .orElseThrow(() -> UserNotFoundException.withId(event.userId()));

    Notification notification = new Notification(
        user,
        "권한이 변경되었습니다.",
        event.oldRole() + " -> " + event.newRole()
    );
    notificationRepository.save(notification);
    cacheManager.getCache("notifications").evict(event.userId());

    NotificationDto notificationDto = notificationMapper.toDto(notification);
    sseService.send(List.of(event.userId()), "notifications.created", notificationDto);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onBinaryContentCreated(BinaryContentCreatedEvent event) {
    BinaryContentDto binaryContentDto = binaryContentMapper.toDto(
        binaryContentRepository.findById(event.binaryContentId()).orElseThrow()
    );
    sseService.broadcast("binaryContents.updated", binaryContentDto);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onChannelEvent(ChannelEvent event) {
    sseService.broadcast(event.eventName(), event.channelDto());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onUserEvent(UserEvent event) {
    sseService.broadcast(event.eventName(), event.userDto());
  }
}
