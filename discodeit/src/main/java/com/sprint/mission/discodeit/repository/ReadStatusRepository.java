package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);
    List<ReadStatus> findAll();
    Optional<ReadStatus> findById(UUID id);
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
