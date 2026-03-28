package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.RequestCreateReadStatusDto;
import com.sprint.mission.discodeit.dto.response.ResponseReadStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    List<ResponseReadStatus> create(RequestCreateReadStatusDto request);

    Instant find(UUID id);

    List<ResponseReadStatus> findAllByUserId(UUID userId);

    ResponseReadStatus update(UUID userId, UUID channelId);

    void deleteForChannel(UUID channelId);

    void deleteForUser(UUID userId);
}
