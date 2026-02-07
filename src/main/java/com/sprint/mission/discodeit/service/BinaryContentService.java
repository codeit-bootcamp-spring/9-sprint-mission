package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDTO create(UUID userId, UUID messageId, BinaryContentCreateRequest request);
    BinaryContentDTO find(UUID id);
    List<BinaryContentDTO> findAllByIdIn(List<UUID> ids);
    void delete(UUID id);
}
