package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    void save(ReadStatus readStatus);

    boolean remove(UUID id);

    Optional<ReadStatus> findByID(UUID id);

    List<ReadStatus> findAll();

    List<ReadStatus> findByChannelID(UUID channelId);
}
