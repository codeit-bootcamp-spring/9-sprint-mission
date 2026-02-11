package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusResponse;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    ReadStatusResponse markAsRead(
            UUID userId,
            UUID channelId
    );

    ReadStatusResponse findByUserAndChannel(
            UUID userId,
            UUID channelId
    );

    List<ReadStatusResponse> findAllByUserId(UUID userId);

    void deleteByUserAndChannel(
            UUID userId,
            UUID channelId
    );
}
