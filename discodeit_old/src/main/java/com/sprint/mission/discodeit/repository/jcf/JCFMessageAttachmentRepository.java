package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.repository.MessageAttachmentRepository;

import java.util.*;

public class JCFMessageAttachmentRepository implements MessageAttachmentRepository {

    private final Map<UUID, List<UUID>> links = new HashMap<>();

    @Override
    public void link(UUID messageId, UUID binaryContentId) {
        links.computeIfAbsent(messageId, k -> new ArrayList<>()).add(binaryContentId);
    }

    @Override
    public List<UUID> findAllAttachmentIdsByMessageId(UUID messageId) {
        return new ArrayList<>(links.getOrDefault(messageId, List.of()));
    }

    @Override
    public int deleteAllByMessageId(UUID messageId) {
        List<UUID> removed = links.remove(messageId);
        return removed == null ? 0 : removed.size();
    }
}

