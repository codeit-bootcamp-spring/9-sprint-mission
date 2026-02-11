package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatus create(ReadStatusDto.CreateDto createDto);
    ReadStatus find(UUID id);
    List<ReadStatus> findAllByUserId(UUID userId);
    ReadStatus update(ReadStatusDto.UpdateDto updateDto);
    void delete(UUID id);
}
