package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  @EntityGraph(attributePaths = {"user", "channel"})
  List<ReadStatus> findAllByUserId(UUID userId);

  @EntityGraph(attributePaths = {"user", "user.status"})
  List<ReadStatus> findAllByChannelIdIn(List<UUID> channelIds);

  Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);


  Boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);


  void deleteAllByChannelId(UUID channelId);
}
