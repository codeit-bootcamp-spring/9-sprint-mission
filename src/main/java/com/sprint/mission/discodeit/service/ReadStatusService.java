package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    List<ReadStatusDto> findAllByUserId(UUID userId);

    ReadStatusDto findById(UUID readStatusId);

    ReadStatusDto create(ReadStatusCreateRequest request);

    ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request);

    ReadStatusDto markAsRead(UUID userId, UUID channelId);

    ReadStatusDto markAsReadById(UUID readStatusId);

    ReadStatusDto findByUserAndChannel(UUID userId, UUID channelId);
}