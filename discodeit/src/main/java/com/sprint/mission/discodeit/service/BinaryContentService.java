package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.service.DTO.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.status.adds.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(BinaryContentCreateRequest request);
    BinaryContent find(UUID id);
    List<BinaryContent> findAllByIdIn(List<UUID> ids);
    void delete(UUID id);
}
