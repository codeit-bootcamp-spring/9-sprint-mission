package com.sprint.mission.discodeit.status;

import com.sprint.mission.discodeit.status.adds.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentInterface {
    void save(BinaryContent binaryContent);
    Optional<BinaryContent> findById(UUID id);
    List<BinaryContent> findAllByIdIn(List<UUID> ids);
    void deleteById(UUID id);
    void deleteByMessageId(UUID messageId);
}
