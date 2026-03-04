package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    ReadStatus create(ReadStatusCreateRequest request);

    ReadStatus find(UUID id);

    List<ReadStatus> findAllbyUserId(UUID id);

    ReadStatus update(UUID id, ReadStatusUpdateRequest request);

    void delete(UUID id);
}
