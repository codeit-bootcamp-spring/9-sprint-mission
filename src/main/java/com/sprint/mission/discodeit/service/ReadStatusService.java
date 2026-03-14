package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.*;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    List<ReadStatusResponse> findAllByUserId(UUID userId);

    ReadStatusResponse findById(UUID readStatusId);

    ReadStatusResponse create(ReadStatusCreateRequest request);

    ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request);

    ReadStatusResponse markAsRead(UUID userId, UUID channelId);

    ReadStatusResponse markAsReadById(UUID readStatusId);

    ReadStatusResponse findByUserAndChannel(UUID userId, UUID channelId);
}