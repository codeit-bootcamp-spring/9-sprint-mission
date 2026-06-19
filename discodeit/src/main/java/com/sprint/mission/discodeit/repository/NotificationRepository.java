package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Notification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  @Query("SELECT n FROM Notification n "
      + "JOIN FETCH n.receiver "
      + "WHERE n.receiver.id = :receiverId "
      + "ORDER BY n.createdAt DESC")
  List<Notification> findAllByReceiverIdWithReceiver(@Param("receiverId") UUID receiverId);

  @Query("SELECT n FROM Notification n "
      + "JOIN FETCH n.receiver "
      + "WHERE n.id = :notificationId")
  Optional<Notification> findByIdWithReceiver(@Param("notificationId") UUID notificationId);

  boolean existsByReceiverIdAndEventKey(UUID receiverId, String eventKey);
}
