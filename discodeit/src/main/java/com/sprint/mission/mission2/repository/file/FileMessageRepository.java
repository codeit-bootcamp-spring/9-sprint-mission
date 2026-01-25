package com.sprint.mission.mission2.repository.file;

import com.sprint.mission.mission2.entity.Message;
import com.sprint.mission.mission2.entity.User;
import com.sprint.mission.mission2.repository.MessageRepository;
import com.sprint.mission.mission2.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private static final String messageData = "message.dat";

    private Map<UUID, Message> load() {
        File file = new File(messageData);

        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("불러오기 실패", e);
        }
    }

    @Override
    public void save(Message message) {
        Map<UUID, Message> messages = load();
        messages.put(message.getId(), message);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(messageData))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            throw new RuntimeException("저장 실패", e);
        }
    }

    @Override
    public Message read(UUID id) {
        Map<UUID, Message> messages = load();
        return messages.get(id);
    }

    @Override
    public List<Message> readAll() {
        Map<UUID, Message> messages = load();
        return new ArrayList<>(messages.values());
    }

    @Override
    public void remove(UUID id) {
        Map<UUID, Message> messages = load();
        messages.remove(id);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(messageData))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            throw new RuntimeException("저장 실패", e);
        }
    }


}
