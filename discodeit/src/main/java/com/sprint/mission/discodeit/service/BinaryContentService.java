package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.BinaryContentService.Request.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(CreateBinaryContentRequest request);

    BinaryContent find(UUID id);

    List<BinaryContent> findAllByIn(List<UUID> idList);

    void delete(UUID id);
}
