package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BinaryContentRepository {
    BinaryContent create(BinaryContent binaryContent);

    Optional<BinaryContent> findById(UUID id);

    Optional<BinaryContent> findAllByIdIn(Iterable<UUID> ids);

    BinaryContent save(BinaryContent binaryContent);

    void deleteById(UUID id);

}

