package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private final String fileName = "messages.dat";

    @Override
    public Message save(Message message) {
        List<Message> messages = findAll();
        messages.removeIf(m -> m.getId().equals(message.getId()));
        messages.add(message);

        saveAllToFile(messages);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return findAll().stream()
                .filter(message -> message.getId().equals(id))
                .findFirst();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Message> findAll() {
        File file = new File(fileName);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean existsById(UUID id) {return findById(id).isPresent();}

    @Override
    public void deleteById(UUID id) {
        List<Message> messages = findAll();
        if (messages.removeIf(message -> message.getId().equals(id))) {
            saveAllToFile(messages);
        }
    }

    private void saveAllToFile(List<Message> messages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}