package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface BinaryContentService {

  BinaryContentDto create(BinaryContentCreateRequest request);

  BinaryContentDto findById(UUID id);

  List<BinaryContentDto> findAllByIdIn(List<UUID> ids);

  void delete(UUID id);

  ResponseEntity<Resource> download(UUID binaryContentId);
}