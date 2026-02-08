package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> data = new HashMap<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        if (binaryContent == null) {
            throw new IllegalArgumentException("binaryContent is null");
        }
        data.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
        if (binaryContentIds == null || binaryContentIds.isEmpty()) {
            return List.of();
        }

        List<BinaryContent> result = new ArrayList<>();
        for (UUID id : binaryContentIds) {
            if (id == null) continue;
            BinaryContent found = data.get(id);
            if (found != null) {
                result.add(found);
            }
        }
        return result;
    }

    @Override
    public void delete(UUID id) {
        if (id == null) return;
        data.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        if (id == null) return false;
        return data.containsKey(id);
    }
}
