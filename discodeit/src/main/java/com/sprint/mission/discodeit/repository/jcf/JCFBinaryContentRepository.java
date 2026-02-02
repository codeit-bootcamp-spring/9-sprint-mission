package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> binaryContentMap;

    public JCFBinaryContentRepository(){
        binaryContentMap = new HashMap<>();
    }

    @Override
    public void save(BinaryContent binaryContent) {
        UUID id = binaryContent.getId();
        binaryContentMap.put(id, binaryContent);
    }

    @Override
    public boolean remove(UUID id) {
        return (binaryContentMap.remove(id) != null);
    }

    @Override
    public BinaryContent findByID(UUID id) {
        return binaryContentMap.get(id);
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(binaryContentMap.values());
    }
}
