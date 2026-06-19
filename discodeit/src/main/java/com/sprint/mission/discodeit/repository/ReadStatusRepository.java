package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  Optional<ReadStatus> findByUser_IdAndChannel_Id(UUID userId, UUID channelId);

  List<ReadStatus> findAllByUser_Id(UUID userId);

  @Query("SELECT rs FROM ReadStatus rs "
      + "JOIN FETCH rs.user "
      + "WHERE rs.channel.id = :channelId")
  List<ReadStatus> findAllByChannel_IdWithUser(@Param("channelId") UUID channelId);

  @Query("SELECT rs FROM ReadStatus rs "
      + "JOIN FETCH rs.user "
      + "JOIN FETCH rs.channel "
      + "WHERE rs.channel.id = :channelId "
      + "AND rs.notificationEnabled = true")
  List<ReadStatus> findAllNotificationEnabledByChannelIdWithUser(
      @Param("channelId") UUID channelId);

  List<ReadStatus> findAllByChannel_Id(UUID channelId);

  void deleteAllByChannel_Id(UUID channelId);
}
