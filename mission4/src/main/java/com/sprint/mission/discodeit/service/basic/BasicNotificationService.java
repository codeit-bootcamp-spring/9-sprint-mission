package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BasicNotificationService {

  private final NotificationRepository repository;

  public List<NotificationDto> getNotification(UUID receiverId) {
    return repository.findAllByReceiverId(receiverId)
        .stream().map(NotificationDto::from)
        .toList();
  }

  public void deleteNotification(UUID id, UUID receiverId) {
    Notification notification = repository.findByIdAndReceiverId(id, receiverId)
        .orElseThrow(() -> new EntityNotFoundException("알림을 찾을 수 없습니다."));
    log.warn("잘못된 알림:{}", id);
    repository.delete(notification);
    log.info("알림 삭제 완료: ID={}, 수신자={}", id, receiverId);

  }


}
