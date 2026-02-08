package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.UUID;

@Repository
@Profile("file")
public class FileUserStatusRepository implements UserStatusRepository {

    private final File file;
    private final Map<UUID, UserStatus> data;

    public FileUserStatusRepository() {
        Path directory = Path.of(System.getProperty("user.dir"), "file-data", "user-status");
        File dir = directory.toFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("user-status 디렉토리 생성 실패: " + dir.getAbsolutePath());
        }
        this.file = directory.resolve("user-statuses.ser").toFile();
        this.data = load();
    }

    @Override
    public UserStatus save(UserStatus status) {
        data.put(status.getId(), status);
        persist();
        return status;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return data.values().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public UserStatus update(UserStatus status) {
        data.put(status.getId(), status);
        persist();
        return status;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        persist();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, UserStatus> load() {
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, UserStatus>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("user-status 로딩 실패", e);
        }
    }

    private void persist() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("user-status 저장 실패", e);
        }
    }
}
