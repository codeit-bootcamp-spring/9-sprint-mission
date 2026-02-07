package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.*;

public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final List<BinaryContent> storage = new ArrayList<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        storage.add(binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return storage.stream()
                .filter(content -> content.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(storage);
    }

    @Override
    public void deleteById(UUID id) {
        storage.removeIf(content -> content.getId().equals(id));
    }
}

