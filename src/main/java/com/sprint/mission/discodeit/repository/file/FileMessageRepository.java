package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {

    private final File file;
    private final Map<UUID, Message> data;

    public FileMessageRepository(String filePath) {
        this.file = new File(filePath);
        this.data = load();
    }

    @Override
    public Message save(Message message) {
        data.put(message.getId(), message);
        persist();
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public Message update(Message message) {
        data.put(message.getId(), message);
        persist();
        return message;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        persist();
    }

    @Override
    public List<Message> findBySenderId(UUID senderId) {
        return data.values().stream()
                .filter(message -> message.getSenderId().equals(senderId))
                .toList();
    }


    @SuppressWarnings("unchecked")
    private Map<UUID, Message> load() {
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("메시지 로딩 실패", e);
        }
    }

    private void persist() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("메시지 저장 실패", e);
        }
    }
}
