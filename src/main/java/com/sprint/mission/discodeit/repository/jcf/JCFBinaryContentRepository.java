package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final List<BinaryContent> contents = new ArrayList<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        contents.add(binaryContent);
        return binaryContent;
    }

    @Override
    public BinaryContent find(UUID id) {
        for (BinaryContent content : contents) {
            if (content.getId().equals(id)) {
                return content;
            }
        }
        return null;
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> result = new ArrayList<>();
        for (BinaryContent content : contents) {
            if (ids.contains(content.getId())) {
                result.add(content);
            }
        }
        return result;
    }

    @Override
    public void delete(UUID id) {
        contents.removeIf(content -> content.getId().equals(id));
    }
}
