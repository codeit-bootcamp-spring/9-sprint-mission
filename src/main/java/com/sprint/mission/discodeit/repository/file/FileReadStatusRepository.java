package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.UUID;

@Repository
@Profile("file")
public class FileReadStatusRepository implements ReadStatusRepository {

    private final File file;
    private final Map<UUID, ReadStatus> data;

    public FileReadStatusRepository() {
        Path directory = Path.of(System.getProperty("user.dir"), "file-data", "read-status");
        File dir = directory.toFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("read-status 디렉토리 생성 실패: " + dir.getAbsolutePath());
        }
        this.file = directory.resolve("read-statuses.ser").toFile();
        this.data = load();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);
        persist();
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return data.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId) && readStatus.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return data.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public ReadStatus update(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);
        persist();
        return readStatus;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        persist();
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        List<UUID> toRemove = data.values().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .map(ReadStatus::getId)
                .toList();
        toRemove.forEach(data::remove);
        if (!toRemove.isEmpty()) persist();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, ReadStatus> load() {
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, ReadStatus>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("read-status 로딩 실패", e);
        }
    }

    private void persist() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("read-status 저장 실패", e);
        }
    }
}
