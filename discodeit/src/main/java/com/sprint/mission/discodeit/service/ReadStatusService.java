package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.ReadStatusService.Request.CreateReadStatusRequest;
import com.sprint.mission.discodeit.DTO.ReadStatusService.Request.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    ReadStatus create(CreateReadStatusRequest request);

    ReadStatus find(UUID id);

    List<ReadStatus> findAllbyUserId(UUID id);

    ReadStatus update(UpdateReadStatusRequest request);

    void delete(UUID id);
}
