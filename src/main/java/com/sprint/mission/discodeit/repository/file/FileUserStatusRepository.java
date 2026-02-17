package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
public class FileUserStatusRepository implements UserStatusRepository {

    private static final String FILE_NAME = "user_statuses.dat";
    private final File baseDir;
    private List<UserStatus> userStatuses;

    public FileUserStatusRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}") String directory
    ) {
        this.baseDir = new File(directory);
        this.userStatuses = load();
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        userStatuses.add(userStatus);
        persist();
        return userStatus;
    }

    @Override
    public UserStatus find(UUID id) {
        for (UserStatus userStatus : userStatuses) {
            if (userStatus.getId().equals(id)) {
                return userStatus;
            }
        }
        return null;
    }

    @Override
    public UserStatus findByUserId(UUID userId) {
        for (UserStatus userStatus : userStatuses) {
            if (userStatus.getUserId().equals(userId)) {
                return userStatus;
            }
        }
        return null;
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(userStatuses);
    }

    @Override
    public void delete(UUID id) {
        userStatuses.removeIf(userStatus -> userStatus.getId().equals(id));
        persist();
    }

    private void persist() {
        ensureDirectory();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(resolveFile()))) {
            oos.writeObject(userStatuses);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<UserStatus> load() {
        File file = resolveFile();
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                List<?> list = (List<?>) obj;
                List<UserStatus> result = new ArrayList<>();
                for (Object o : list) {
                    if (o instanceof UserStatus) {
                        result.add((UserStatus) o);
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
