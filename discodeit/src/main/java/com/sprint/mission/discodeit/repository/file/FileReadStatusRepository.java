package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.io.*;
import java.util.*;
public class FileReadStatusRepository implements ReadStatusRepository {

    private final String filePath;
    private Map<UUID, ReadStatus> store;
    public FileReadStatusRepository(String filePath) {
        this.filePath = filePath;

        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        this.store = loadData();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, ReadStatus> loadData() {
        File file = new File(this.filePath);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, ReadStatus>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[FileReadStatusRepository Error] 로드 실패: " + e.getMessage());
            return new HashMap<>();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.filePath))) {
            oos.writeObject(store);
        } catch (IOException e) {
            System.err.println("[FileReadStatusRepository Error] 저장 실패: " + e.getMessage());
        }
    }
    @Override
    public void save(ReadStatus readStatus) {
        store.put(readStatus.getId(), readStatus);
        saveData();
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus rs : store.values()) {
            if (rs.getUserId().equals(userId)) {
                result.add(rs);
            }
        }
        return result;
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        for (ReadStatus rs : store.values()) {
            if (rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId)) {
                return Optional.of(rs);
            }
        }
        return Optional.empty();
    }

    @Override
    public void delete(UUID id) {
        if (store.remove(id) != null) {
            saveData();
        }
    }
}