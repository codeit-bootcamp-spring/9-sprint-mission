package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.*;

public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> store = new HashMap<>();

    @Override
    public void create(BinaryContent content) {
        store.put(content.getId(), content);
    }

    @Override
    public BinaryContent findById(UUID id) {
        return store.get(id);
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean delete(UUID id) {
        return store.remove(id) != null;
    }
}

