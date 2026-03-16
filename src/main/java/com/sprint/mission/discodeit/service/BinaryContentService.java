package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  BinaryContentDto create(BinaryContentCreateRequest request);      // BinaryContent → BinaryContentDto

  BinaryContentDto find(UUID binaryContentId);                       // BinaryContent → BinaryContentDto

  List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds); // List<BinaryContent> → List<BinaryContentDto>

  void delete(UUID binaryContentId);
}
