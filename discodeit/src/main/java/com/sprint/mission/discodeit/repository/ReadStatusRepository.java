package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {

    void create(ReadStatus readStatus);

    ReadStatus findById(UUID id);

    /**
     * userId로 조회(요구사항: findAllByUserId)
     */
    List<ReadStatus> findAllByUserId(UUID userId);

    /**
     * userId + channelId로 단건 조회(중복 생성 방지/조회용)
     */
    ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId);

    /**
     * 마지막 읽은 시간 갱신
     */
    boolean update(UUID id, long lastReadAt);

    boolean delete(UUID id);
}

