package com.sprint.mission.discordit.service;

import com.sprint.mission.discordit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discordit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  BinaryContent create(BinaryContentCreateRequest request);

  BinaryContent find(UUID binaryContentId);

  List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds);

  void delete(UUID binaryContentId);
}
