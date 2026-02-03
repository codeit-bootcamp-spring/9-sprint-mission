package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.status.BinaryContentInterface;
import com.sprint.mission.discodeit.status.adds.BinaryContent;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
public class JCFBinaryContentRepository implements BinaryContentInterface {
    private final Map<UUID, BinaryContent> store = new HashMap<>();

    @Override
    public void save(BinaryContent binaryContent) {
        store.put(binaryContent.getId(), binaryContent);
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(store::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }

    @Override
    public void deleteByMessageId(UUID messageId) {
        store.remove(messageId);
    }
}
