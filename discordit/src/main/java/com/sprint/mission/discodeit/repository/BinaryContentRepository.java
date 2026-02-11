package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.CreateBinaryContentDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository {
    UUID create(CreateBinaryContentDto requestDto);

    String find(UUID id);

    List<String> findAllByIdIn(List<UUID> ids);

    boolean delete(UUID id);

    void delete(List<UUID> ids);
}
