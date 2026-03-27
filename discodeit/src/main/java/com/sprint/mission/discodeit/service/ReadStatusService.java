package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatusResponse create(ReadStatusCreateRequest request);

  ReadStatusResponse find(UUID readStatusId);

  List<ReadStatusResponse> findAllByUserId(UUID userId);

  ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request);

  void delete(UUID readStatusId);
}
