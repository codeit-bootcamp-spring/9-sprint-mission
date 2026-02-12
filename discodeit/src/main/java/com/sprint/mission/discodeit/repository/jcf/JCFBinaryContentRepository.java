package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.*;

public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> binaryContentMap = new HashMap<>();

    @Override
    public void save(BinaryContent binaryContent) {
        binaryContentMap.put(binaryContent.getId(), binaryContent);
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(binaryContentMap.get(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> result = new ArrayList<>();
        for (BinaryContent content : binaryContentMap.values()) {
            if (ids.contains(content.getId())) {
                result.add(content);
            }
        }
        return result;
    }

    @Override
    public void delete(UUID id) {
        binaryContentMap.remove(id);
    }
}