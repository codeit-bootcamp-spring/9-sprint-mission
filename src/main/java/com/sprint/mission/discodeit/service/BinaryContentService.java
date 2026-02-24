package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponse create(String fileName, byte[] data, String contentType);
    BinaryContentResponse findById(UUID id);
    BinaryContent findEntityById(UUID id);
    List<BinaryContentResponse> findAllByIdIn(List<UUID> ids);
    void delete(UUID id);
}
