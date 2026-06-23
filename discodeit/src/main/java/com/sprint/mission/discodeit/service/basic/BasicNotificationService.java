package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.NotificationCreatedEvent;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final ReadStatusRepository readStatusRepository;
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final NotificationMapper notificationMapper;
  private final CacheManager cacheManager;
  private final ApplicationEventPublisher eventPublisher;


  @Override
  public void createByMessage(MessageDto messageDto) {
    Message message = messageRepository.findById(messageDto.id())
        .orElseThrow(() -> new MessageNotFoundException(messageDto.id()));
    String title = message.getAuthor().getUsername() + " (#" + message.getChannel().getName() + ")";
    String content = message.getContent();
    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannel(message.getChannel());
    List<ReadStatus> targets = readStatuses.stream().filter(ReadStatus::isNotificationEnabled)
        .toList();
    List<User> users = targets.stream().map(ReadStatus::getUser).toList();

    var cache = cacheManager.getCache("NotificationList");

    List<NotificationDto> dtos = new ArrayList<>();
    for (User u : users) {
      if (u.getId().equals(message.getAuthor().getId())) {
        continue;
      }
      Notification notification = new Notification(u.getId(), title, content);
      notificationRepository.save(notification);
      NotificationDto dto = notificationMapper.toDto(notification);
      dtos.add(dto);

      if (cache != null) {
        String cacheKey = "notifications_" + u.getId();
        cache.evict(cacheKey);
      }
    }

    eventPublisher.publishEvent(new NotificationCreatedEvent(dtos));
    log.info("알림 생성 완료");
  }

  @Override
  public void createByRole(User user, Role pastRole, Role newRole) {
    String title = "권한이 변경되었습니다.";
    String content = pastRole.toString() + " -> " + newRole.toString();
    Notification notification = new Notification(user.getId(), title, content);
    notificationRepository.save(notification);
    NotificationDto dto = notificationMapper.toDto(notification);
    eventPublisher.publishEvent(new NotificationCreatedEvent(List.of(dto)));

    var cache = cacheManager.getCache("NotificationList");
    if (cache != null) {
      String cacheKey = "notifications_" + user.getId();
      cache.evict(cacheKey);
    }
  }

  @Override
  public void createByError(String errorMessage) {
    List<User> admin = userRepository.findByRole(Role.ADMIN);
    var cache = cacheManager.getCache("NotificationList");

    List<NotificationDto> dtos = new ArrayList<>();
    for (User a : admin) {
      Notification notification = new Notification(a.getId(), "S3 파일 업로드 실패", errorMessage);
      notificationRepository.save(notification);
      NotificationDto dto = notificationMapper.toDto(notification);
      dtos.add(dto);

      if (cache != null) {
        String cacheKey = "notifications_" + a.getId();
        cache.evict(cacheKey);
      }
    }
    eventPublisher.publishEvent(new NotificationCreatedEvent(dtos));
  }

  @Override
  public List<NotificationDto> get(UUID userId) {
    List<Notification> notifications = notificationRepository.findAllByReceiverId(userId);
    List<NotificationDto> dtos = notifications.stream()
        .map(n -> {
          NotificationDto dto = new NotificationDto(
              n.getId(),
              n.getCreatedAt(),
              n.getReceiverId(),
              n.getTitle(),
              n.getContent()
          );
          return dto;
        })
        .toList();
    return dtos;
  }

  @Override
  public UUID delete(UUID notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationNotFoundException(notificationId));
    UUID userId = notification.getReceiverId();
    notificationRepository.delete(notification);
    return userId;
  }
}
