package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.AbstractFileRepository;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileBinaryContentRepository extends AbstractFileRepository<BinaryContent> implements BinaryContentRepository {

    private final Path directory;

    public FileBinaryContentRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}") String baseDir
    ) {
        this.directory = Paths.get(
                System.getProperty("user.dir"),
                baseDir,
                BinaryContent.class.getSimpleName()
        );
        ensureDirectory();
    }

    @Override
    protected Path directory() {
        return directory;
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        if (binaryContent == null) {
            throw new IllegalArgumentException("binaryContent is null");
        }
        write(resolvePath(binaryContent.getId()), binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        if (id == null) return Optional.empty();
        Path path = resolvePath(id);
        if (!exists(path)) return Optional.empty();
        return Optional.of(read(path));
    }

    @Override
    public void delete(UUID id) {
        if (id == null) return;
        delete(resolvePath(id));
    }

    @Override
    public boolean existsById(UUID id) {
        if (id == null) return false;
        return exists(resolvePath(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<BinaryContent> result = new ArrayList<>();
        for (UUID id : ids) {
            if (id == null) continue;
            Path path = resolvePath(id);
            if (exists(path)) {
                result.add(read(path));
            }
        }
        return result;
    }
}
