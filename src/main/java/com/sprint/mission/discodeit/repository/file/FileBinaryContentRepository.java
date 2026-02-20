package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileBinaryContentRepository extends AbstractFileRepository<BinaryContent> implements BinaryContentRepository {

    public FileBinaryContentRepository(
        @Value("${discodeit.repository.file-directory:data}") String fileDirectory,
        FileLockProvider fileLockProvider) {
        super(fileDirectory, BinaryContent.class, fileLockProvider);
    }

    @Override
    protected UUID getId(BinaryContent entity) {
        return entity.getId();
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return findAll().stream()
            .filter(c -> ids.contains(c.getId()))
            .toList();
    }
}
