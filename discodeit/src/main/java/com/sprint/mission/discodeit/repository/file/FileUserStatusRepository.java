package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {

    private static final String FILE_PATH = "user-status.dat";

    private Map<UUID, UserStatus> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, UserStatus>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void saveAll(Map<UUID, UserStatus> store) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(store);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        Map<UUID, UserStatus> store = load();
        store.put(userStatus.getId(), userStatus);
        saveAll(store);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(load().get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return load().values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(load().values());
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return load().values().stream()
                .anyMatch(status -> status.getUserId().equals(userId));
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, UserStatus> store = load();
        store.remove(id);
        saveAll(store);
    }
}
