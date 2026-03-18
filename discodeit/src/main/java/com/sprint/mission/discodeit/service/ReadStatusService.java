package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    ReadStatusDto create(ReadStatusCreateRequest request);

    ReadStatusDto find(UUID id);

    List<ReadStatusDto> findAllbyUserId(UUID id);

    ReadStatusDto update(UUID id, ReadStatusUpdateRequest request);

    void delete(UUID id);
}
