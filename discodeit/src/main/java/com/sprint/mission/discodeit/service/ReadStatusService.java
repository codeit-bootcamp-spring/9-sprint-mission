package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  List<ReadStatusDto> findAllByUserId(UUID userId);

  ReadStatusDto create(ReadStatusCreateRequest request);

  ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request);
}