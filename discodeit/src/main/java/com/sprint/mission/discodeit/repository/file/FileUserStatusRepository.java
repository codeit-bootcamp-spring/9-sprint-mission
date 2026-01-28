package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;
import java.io.*;
import java.util.*;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    private final String FILE_PATH = "user_status.ser";
    private Map<UUID, UserStatus> statusMap;

    public FileUserStatusRepository() {
        this.statusMap = loadData();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, UserStatus> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, UserStatus>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(statusMap);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void save(UserStatus userStatus) {
        statusMap.put(userStatus.getId(), userStatus);
        saveData();
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(statusMap.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return statusMap.values().stream()
                .filter(s -> s.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(statusMap.values());
    }

    @Override
    public void delete(UUID id) {
        if (statusMap.remove(id) != null) saveData();
    }
}