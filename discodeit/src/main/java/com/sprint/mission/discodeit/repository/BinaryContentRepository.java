package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository {

    void create(BinaryContent binaryContent);

    BinaryContent findById(UUID id);

    List<BinaryContent> findAllById(List<UUID> ids);

    boolean delete(UUID id);
}

