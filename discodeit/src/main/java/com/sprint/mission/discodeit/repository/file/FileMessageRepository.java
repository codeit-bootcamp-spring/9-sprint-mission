package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private final String filePath;
    private Map<UUID, Message> messageMap;
    public FileMessageRepository(String filePath) {
        this.filePath = filePath;
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        this.messageMap = loadData();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Message> loadData() {
        File file = new File(this.filePath);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[FileMessageRepository Error] 로드 실패: " + e.getMessage());
            return new HashMap<>();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.filePath))) {
            oos.writeObject(messageMap);
        } catch (IOException e) {
            System.err.println("[FileMessageRepository Error] 저장 실패: " + e.getMessage());
        }
    }

    @Override
    public void save(Message message) {
        messageMap.put(message.getId(), message);
        saveData();
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(messageMap.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messageMap.values());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        List<Message> result = new ArrayList<>();
        for (Message m : messageMap.values()) {
            if (m.getChannelId().equals(channelId)) {
                result.add(m);
            }
        }
        return result;
    }

    @Override
    public Optional<Message> findLatestByChannelId(UUID channelId) {
        Message latest = null;
        for (Message message : messageMap.values()) {
            if (message.getChannelId().equals(channelId)) {
                if (latest == null || message.getCreatedAt().isAfter(latest.getCreatedAt())) {
                    latest = message;
                }
            }
        }
        return Optional.ofNullable(latest);
    }

    @Override
    public void delete(UUID id) {
        if (messageMap.remove(id) != null) {
            saveData();
        }
    }
}