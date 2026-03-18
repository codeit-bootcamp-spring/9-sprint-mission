package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReadStatusJpaRepository extends JpaRepository<ReadStatus, UUID> {
  void deleteAllByChannelId(UUID channelId);
  List<ReadStatus> findAllByUserId(UUID userId);
  List<ReadStatus> findAllByChannelId(UUID channelId);
}
