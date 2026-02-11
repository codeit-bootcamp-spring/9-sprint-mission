package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


public interface BinaryContentRepository {

    Optional<BinaryContent> findById(UUID id);
    Optional<BinaryContent> findByUserId(UUID userId);
    void deleteById(UUID id);
    BinaryContent save(BinaryContent binaryContent);
}
