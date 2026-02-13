package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.AbstractFileRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileReadStatusRepository extends AbstractFileRepository<ReadStatus> implements ReadStatusRepository {

    private final Path directory;
    private final Path indexByUserDirectory;

    public FileReadStatusRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}") String baseDir
    ) {
        this.directory = Paths.get(
                System.getProperty("user.dir"),
                baseDir,
                ReadStatus.class.getSimpleName()
        );

        this.indexByUserDirectory = Paths.get(
                System.getProperty("user.dir"),
                baseDir,
                "ReadStatus-index",
                "by-user"
        );

        ensureDirectory();
        ensureIndexDirectories();
    }

    @Override
    protected Path directory() {
        return directory;
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        if (readStatus == null) {
            throw new IllegalArgumentException("readStatus is null");
        }

        write(resolvePath(readStatus.getId()), readStatus);
        upsertUserIndex(readStatus.getUserId(), readStatus.getId());

        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        if (id == null) return Optional.empty();
        Path path = resolvePath(id);
        if (!exists(path)) return Optional.empty();
        return Optional.of(read(path));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        if (userId == null) {
            return List.of();
        }

        List<UUID> ids = readUserIndex(userId);
        if (ids.isEmpty()) {
            return List.of();
        }

        List<ReadStatus> result = new ArrayList<>();
        for (UUID id : ids) {
            if (id == null) continue;
            Path path = resolvePath(id);
            if (exists(path)) {
                result.add(read(path));
            }
        }
        return result;
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            return List.of();
        }

        // by-user 인덱스 파일들을 훑어서 ReadStatus id를 모은 뒤, channelId로 필터링
        if (!Files.exists(indexByUserDirectory)) {
            return List.of();
        }

        Set<UUID> seen = new LinkedHashSet<>();
        try (var paths = Files.list(indexByUserDirectory)) {
            paths.filter(p -> p.getFileName().toString().endsWith(".ser"))
                    .filter(Files::isRegularFile)
                    .forEach(p -> seen.addAll(readUserIndexFromPath(p)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to list user index directory: " + indexByUserDirectory, e);
        }

        if (seen.isEmpty()) {
            return List.of();
        }

        List<ReadStatus> result = new ArrayList<>();
        for (UUID id : seen) {
            if (id == null) continue;
            Path path = resolvePath(id);
            if (!exists(path)) continue;
            ReadStatus rs = read(path);
            if (rs != null && channelId.equals(rs.getChannelId())) {
                result.add(rs);
            }
        }
        return result;
    }

    private List<UUID> readUserIndexFromPath(Path idxPath) {
        return readUuidList(idxPath).orElseGet(ArrayList::new);
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        if (userId == null || channelId == null) {
            return Optional.empty();
        }

        List<ReadStatus> all = findAllByUserId(userId);
        for (ReadStatus rs : all) {
            if (rs != null && channelId.equals(rs.getChannelId())) {
                return Optional.of(rs);
            }
        }
        return Optional.empty();
    }

    @Override
    public void delete(UUID id) {
        if (id == null) return;

        Optional<ReadStatus> existing = findById(id);

        delete(resolvePath(id));

        if (existing.isEmpty()) return;

        UUID userId = existing.get().getUserId();
        removeFromUserIndex(userId, id);
    }

    @Override
    public boolean existsById(UUID id) {
        if (id == null) return false;
        return exists(resolvePath(id));
    }

    // Index helpers
    private void ensureIndexDirectories() {
        try {
            Files.createDirectories(indexByUserDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create index directories", e);
        }
    }

    private Path indexByUserPath(UUID userId) {
        return indexByUserDirectory.resolve(userId.toString() + ".ser");
    }

    private <T extends Serializable> void writeSerializable(Path path, T value) {
        ensureIndexDirectories();
        try (OutputStream os = Files.newOutputStream(path);
             ObjectOutputStream oos = new ObjectOutputStream(os)) {
            oos.writeObject(value);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write index: " + path, e);
        }
    }

    private <T> Optional<T> readSerializable(Path path, Class<T> type) {
        if (!Files.exists(path)) return Optional.empty();
        try (InputStream is = Files.newInputStream(path);
             ObjectInputStream ois = new ObjectInputStream(is)) {
            Object obj = ois.readObject();
            if (obj == null) return Optional.empty();
            if (!type.isInstance(obj)) {
                return Optional.empty();
            }
            return Optional.of(type.cast(obj));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read index: " + path, e);
        }
    }

    private Optional<List<UUID>> readUuidList(Path path) {
        Optional<List> maybe = readSerializable(path, List.class);
        if (maybe.isEmpty()) return Optional.empty();

        List<?> raw = maybe.get();
        List<UUID> result = new ArrayList<>();
        for (Object o : raw) {
            if (o instanceof UUID uuid) {
                result.add(uuid);
            }
        }
        return Optional.of(result);
    }

    private List<UUID> readUserIndex(UUID userId) {
        return readUuidList(indexByUserPath(userId)).orElseGet(ArrayList::new);
    }

    private void upsertUserIndex(UUID userId, UUID readStatusId) {
        List<UUID> ids = readUserIndex(userId);
        if (!ids.contains(readStatusId)) {
            ids.add(readStatusId);
            writeSerializable(indexByUserPath(userId), (Serializable) ids);
        }
    }

    private void removeFromUserIndex(UUID userId, UUID readStatusId) {
        Path idxPath = indexByUserPath(userId);
        if (!Files.exists(idxPath)) return;

        List<UUID> ids = readUserIndex(userId);
        boolean removed = ids.removeIf(id -> Objects.equals(id, readStatusId));
        if (!removed) return;

        if (ids.isEmpty()) {
            try {
                Files.deleteIfExists(idxPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete user index: " + idxPath, e);
            }
            return;
        }

        writeSerializable(idxPath, (Serializable) ids);
    }
}
