package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationAccessDeniedException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;

  @Override
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    List<Notification> notifications = notificationRepository.findByReceiverId(receiverId);
    return notifications.stream().map(notificationMapper::toDto).toList();
  }

  @Override
  @Transactional
  public void delete(UUID notificationId, UUID currentUserId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(NotificationNotFoundException::new);

    if (!notification.getReceiverId().equals(currentUserId)) {
      throw new NotificationAccessDeniedException();
    }

    notificationRepository.delete(notification);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void createMessageNotification(UUID uuid) {
    Message message = messageRepository.findById(uuid)
        .orElseThrow(() -> new NoSuchElementException("메시지가 존재하지 않습니다."));

    UUID channelId = message.getChannel().getId();
    UUID authorId = message.getAuthor().getId();
    String channelName = message.getChannel().getName();

    List<ReadStatus> activeReadStatuses = readStatusRepository
        .findByChannelIdAndNotificationEnabledTrue(channelId);

    List<Notification> notifications = activeReadStatuses.stream()
        .map(ReadStatus::getUser)
        .filter(user -> !user.getId().equals(authorId))
        .map(receiver -> new Notification(
            receiver.getId(),
            "보낸 사람 (#" + message.getAuthor().getUsername() + ")",
            message.getContent()
        ))
        .toList();

    notificationRepository.saveAll(notifications);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void createRoleUpdatedNotification(UUID uuid) {
    User user = userRepository.findById(uuid)
        .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않습니다."));

    Notification notification = new Notification(
        user.getId(),
        "권한이 변경되었습니다.",
        "이전권한 -> " + user.getRole()
    );

    notificationRepository.save(notification);
  }
}
