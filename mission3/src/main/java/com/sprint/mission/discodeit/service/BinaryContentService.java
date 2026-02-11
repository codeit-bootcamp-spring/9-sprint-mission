package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(BinaryContentDto.createDto createDto);
    BinaryContent find(UUID id);
    List<BinaryContent> findAllByIdIn(List<UUID> ids);
    boolean delete(UUID id);


}
