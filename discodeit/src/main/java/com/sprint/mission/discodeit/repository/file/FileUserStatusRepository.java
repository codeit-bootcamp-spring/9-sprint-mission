package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.io.*;
import java.util.*;

public class FileUserStatusRepository implements UserStatusRepository {
    private final String filePath; // [수정] 변수로 변경
    private Map<UUID, UserStatus> statusMap;
    public FileUserStatusRepository(String filePath) {
        this.filePath = filePath;

        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        this.statusMap = loadData();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, UserStatus> loadData() {
        File file = new File(this.filePath);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, UserStatus>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[FileUserStatusRepository Error] 로드 실패: " + e.getMessage());
            return new HashMap<>();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.filePath))) {
            oos.writeObject(statusMap);
        } catch (IOException e) {
            System.err.println("[FileUserStatusRepository Error] 저장 실패: " + e.getMessage());
        }
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
        for (UserStatus s : statusMap.values()) {
            if (s.getUserId().equals(userId)) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
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