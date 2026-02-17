package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileReadStatusRepository implements ReadStatusRepository {

    private static final String FILE_NAME = "read_statuses.dat";
    private final File baseDir;
    private List<ReadStatus> readStatuses;

    public FileReadStatusRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}") String directory
    ) {
        this.baseDir = new File(directory);
        this.readStatuses = load();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        readStatuses.add(readStatus);
        persist();
        return readStatus;
    }

    @Override
    public ReadStatus find(UUID id) {
        for (ReadStatus readStatus : readStatuses) {
            if (readStatus.getId().equals(id)) {
                return readStatus;
            }
        }
        return null;
    }

    @Override
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        for (ReadStatus readStatus : readStatuses) {
            if (readStatus.getUserId().equals(userId) && readStatus.getChannelId().equals(channelId)) {
                return readStatus;
            }
        }
        return null;
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus readStatus : readStatuses) {
            if (readStatus.getUserId().equals(userId)) {
                result.add(readStatus);
            }
        }
        return result;
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(readStatuses);
    }

    @Override
    public void delete(UUID id) {
        readStatuses.removeIf(readStatus -> readStatus.getId().equals(id));
        persist();
    }

    private void persist() {
        ensureDirectory();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(resolveFile()))) {
            oos.writeObject(readStatuses);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ReadStatus> load() {
        File file = resolveFile();
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                List<?> list = (List<?>) obj;
                List<ReadStatus> result = new ArrayList<>();
                for (Object o : list) {
                    if (o instanceof ReadStatus) {
                        result.add((ReadStatus) o);
                    }
                }
                return result;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private File resolveFile() {
        return new File(baseDir, FILE_NAME);
    }

    private void ensureDirectory() {
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }
}
