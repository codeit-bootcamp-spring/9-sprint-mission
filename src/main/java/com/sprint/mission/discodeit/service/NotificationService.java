package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Cacheable(value = "userNotifications", key = "#receiverId")
    public List<NotificationDto> getNotifications(UUID receiverId) {
        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId);
        return notifications.stream()
                .map(NotificationDto::form)
                .collect(Collectors.toList());
    }
    @Transactional
    @CacheEvict(value = "userNotifications", key = "#receiverId")
    public void deleteNotification(UUID id, UUID receiverId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new DiscodeitException(ErrorCode.NOTIFICATION_NOT_FOUND));
        if (!notification.getReceiverId().equals(receiverId)) {
            throw new DiscodeitException(ErrorCode.NOTIFICATION_ACCESS_DENIED);
        }
        notificationRepository.delete(notification);
    }
}
