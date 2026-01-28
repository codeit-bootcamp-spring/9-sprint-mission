package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private final String fileName = "messages.dat";

    @Override
    public Message save(Message message) {
        List<Message> messages = new ArrayList<>(findAll());
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
        List<Message> messages = new ArrayList<>(findAll());
        if (messages.removeIf(m -> m.getId().equals(id))) {
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
