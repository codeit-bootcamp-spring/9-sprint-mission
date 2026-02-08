package com.sprint.mission.discodeit.repository.User;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {

    BinaryContent save(BinaryContent content);

    Optional<BinaryContent> findById(UUID id);

    void deleteByOwnerId(UUID ownerId);
}
