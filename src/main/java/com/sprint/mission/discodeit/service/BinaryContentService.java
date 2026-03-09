package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    BinaryContentDto create(String fileName, byte[] bytes, String contentType);

    BinaryContentDto findById(UUID id);

    BinaryContent findEntityById(UUID id);

    List<BinaryContentDto> findAllByIdIn(List<UUID> ids);

    void delete(UUID id);
}