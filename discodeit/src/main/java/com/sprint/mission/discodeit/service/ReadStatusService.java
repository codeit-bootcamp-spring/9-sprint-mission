package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    ReadStatus create(ReadStatusCreateRequestDto dto);

    ReadStatus find(UUID id);

    List<ReadStatus> findAllByUserId(UUID userId);

    ReadStatus update(UUID id, ReadStatusUpdateRequestDto dto);

    void delete(UUID id);
}
