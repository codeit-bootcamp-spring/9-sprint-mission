package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  List<ReadStatus> findAllByChannel(Channel channel);

  @Query("SELECT MAX(rs.lastReadAt) FROM ReadStatus rs WHERE rs.channel.id = :channelId")
  Optional<Instant> findOldestReadAtByChannelId(@Param("channelId") UUID channelId);

  boolean existsByUserIdAndChannelId(UUID user_id, UUID channel_id);

  List<ReadStatus> findAllByUserId(UUID userId);

  List<ReadStatus> findByChannelIdAndNotificationEnabledTrue(UUID channelId);

  List<ReadStatus> findAllByChannelId(UUID id);
}
