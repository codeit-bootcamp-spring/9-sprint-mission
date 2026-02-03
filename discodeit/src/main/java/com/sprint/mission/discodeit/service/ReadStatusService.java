package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.service.DTO.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.status.adds.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId);

    ReadStatus create(ReadStatusCreateRequest request);
    ReadStatus findByUserId(UUID userId);
    List<ReadStatus> findAllByUserId(UUID userId);
    boolean existByUserIdAndChannelId(UUID userId, UUID channelId);
    void deleteById(UUID id);
}