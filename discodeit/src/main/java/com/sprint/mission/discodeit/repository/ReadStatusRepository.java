package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    ReadStatus save(ReadStatus readStatus);
    Optional<ReadStatus> findById(UUID id);
    List<ReadStatus> findAllByUserId(UUID userId);
    void delete(UUID id);
    boolean existsById(UUID id);

    // 도메인 핵심(유저-채널 1개 상태)
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);
}