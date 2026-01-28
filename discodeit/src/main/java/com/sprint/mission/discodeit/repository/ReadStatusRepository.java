package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    void save(ReadStatus readStatus);
    Optional<ReadStatus> findById(UUID id);
    List<ReadStatus> findAllByUserId(UUID userId); // [추가] 특정 유저의 모든 읽음 상태 조회
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId); // [추가] 중복 생성 방지용
    void delete(UUID id);
}