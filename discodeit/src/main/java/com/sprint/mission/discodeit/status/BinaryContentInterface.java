package com.sprint.mission.discodeit.status;

import com.sprint.mission.discodeit.status.time.BinaryContent;

import java.util.Optional;
import java.util.UUID;

public interface BinaryContentInterface {
    void save(BinaryContent binaryContent);
    Optional<BinaryContent> findById(UUID id);
}
