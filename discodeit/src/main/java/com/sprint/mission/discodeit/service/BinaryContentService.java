package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentService {

  BinaryContent create(BinaryContentCreateRequest request);

  Optional<BinaryContent> findById(UUID id);

  List<BinaryContent> findAllByIdIn(List<UUID> ids);

  void delete(UUID id);
}