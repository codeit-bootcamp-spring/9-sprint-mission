package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  BinaryContentResponse create(BinaryContentCreateRequest request);

  BinaryContentResponse find(UUID binaryContentId);

  List<BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds);

  void delete(UUID binaryContentId);
}
