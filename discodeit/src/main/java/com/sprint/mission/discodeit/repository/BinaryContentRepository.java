package com.sprint.mission.discodeit.repository;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository {
    void save(BinaryContent binaryContent);

    boolean remove(UUID id);

    BinaryContent findByID(UUID id);

    List<BinaryContent> findAll();
}
