package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto get(UUID id);
    BinaryContent create(BinaryContentCreateRequest request);
    BinaryContent find(UUID binaryContentId);
    List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds);
    void delete(UUID binaryContentId);
}
