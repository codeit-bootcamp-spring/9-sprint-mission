package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.request.RequestCreateReadStatusDto;
import com.sprint.mission.discodeit.dto.response.ResponseReadStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {
    List<ResponseReadStatus> create(RequestCreateReadStatusDto request);

    Instant find(UUID id);

    List<ResponseReadStatus> findAllByUserId(UUID userId);

    ResponseReadStatus update(UUID userId, UUID channelId);

    boolean delete(UUID id);

    void deleteForChannel(UUID channelId);

    void deleteForUser(UUID userId);
}
