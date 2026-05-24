package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReadStatusRepository extends JpaRepository<ReadStatus,UUID> {

  List<ReadStatus> findAllByUserId(UUID userId);

  List<ReadStatus> findAllByChannelId(UUID channelId);
  @Query("SELECT rs FROM ReadStatus rs "
      + "JOIN FETCH rs.user "
      + "JOIN FETCH rs.channel "
      + "WHERE rs.user.id = :userId "
      + "AND rs.channel.id = :channelId")
  Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);
  void deleteAllByChannelId(UUID channelId);
}
