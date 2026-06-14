package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
  List<Notification> findAllByReceiverId(UUID receiverId);
}

