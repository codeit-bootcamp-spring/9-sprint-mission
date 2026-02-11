package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JCFBinaryContentRepository implements BinaryContentRepository {
    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        return null;
    }

    @Override
    public List<BinaryContent> findAll() {
        return List.of();
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(UUID id) {
        return false;
    }

    @Override
    public void deleteById(UUID id) {

    }
}
