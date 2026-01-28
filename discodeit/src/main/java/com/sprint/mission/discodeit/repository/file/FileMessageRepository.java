package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;
import java.io.*;
import java.util.*;

@Repository
public class FileMessageRepository implements MessageRepository {
    private final String FILE_PATH = "messages.ser";
    private Map<UUID, Message> messageMap;

    public FileMessageRepository() {
        this.messageMap = loadData();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Message> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(messageMap);
        } catch (IOException e) {
            e.printStackTrace();
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
        return messageMap.values().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .toList();
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