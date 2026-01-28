package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {

    void create(ReadStatus readStatus);

    ReadStatus findById(UUID id);

    List<ReadStatus> findAll();

    // ✅ “userId 기준 조회”
    List<ReadStatus> findAllByUserId(UUID userId);

    // ✅ “channelId 기준 조회”
    List<ReadStatus> findAllByChannelId(UUID channelId);

    // ✅ (userId, channelId) 조합 단건
    ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId);

    boolean delete(UUID id);

    // 채널 삭제 시 같이 지우는 용도
    int deleteAllByChannelId(UUID channelId);
}


