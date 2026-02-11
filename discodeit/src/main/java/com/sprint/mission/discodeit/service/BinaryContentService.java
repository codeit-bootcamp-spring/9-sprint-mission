package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.binary.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    BinaryContent create(BinaryContentCreateRequestDto dto);

    BinaryContent find(UUID id);

    List<BinaryContent> findAllByIdIn(List<UUID> ids);

    void delete(UUID id);
}
