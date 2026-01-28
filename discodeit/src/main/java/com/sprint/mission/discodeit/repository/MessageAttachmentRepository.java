// src/main/java/com/sprint/mission/discodeit/repository/MessageAttachmentRepository.java
package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.UUID;

public interface MessageAttachmentRepository {

    void link(UUID messageId, UUID binaryContentId);

    List<UUID> findAllAttachmentIdsByMessageId(UUID messageId);

    int deleteAllByMessageId(UUID messageId);
}

