package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    BinaryContentDto create(String fileName, byte[] data, String contentType);

    BinaryContentDto findById(UUID id);

    BinaryContent findEntityById(UUID id); // 다운로드용

    List<BinaryContentDto> findAllByIdIn(List<UUID> ids);

    void delete(UUID id);
}