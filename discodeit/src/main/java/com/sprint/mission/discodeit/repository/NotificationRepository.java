package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Notification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  List<Notification> findAllByReceiverIdOrderByCreatedAtDesc(UUID receiverId);

  @Query("SELECT n.receiver.id FROM Notification n WHERE n.id = :notificationId")
  Optional<UUID> findReceiverIdById(@Param("notificationId") UUID notificationId);
}
