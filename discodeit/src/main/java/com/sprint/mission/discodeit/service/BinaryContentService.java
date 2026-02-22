package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentView;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  BinaryContentView create(BinaryContentCreateRequest request);

  BinaryContentView findById(UUID binaryContentId);

  BinaryContent findEntityById(UUID binaryContentId);

  List<BinaryContentView> findAllByIdIn(List<UUID> binaryContentIds);

  void delete(UUID binaryContentId);

  boolean existsById(UUID binaryContentId);
}
