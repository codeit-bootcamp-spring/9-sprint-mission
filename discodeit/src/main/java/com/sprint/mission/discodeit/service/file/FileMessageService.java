package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageSevice;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageSevice {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileMessageService() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", "Message");
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Message create(String name, String messageText, UUID user) {
        Message message = new Message(name, messageText, user);
        saveMessage(message);
        return message;
    }

    private void saveMessage(Message message) {
        Path path = DIRECTORY.resolve(message.getUser() + EXTENSION);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("메시지 저장 실패: " + message, e);
        }
    }

    @Override
    public Message find(UUID userUUID) {
        Path path = DIRECTORY.resolve(userUUID + EXTENSION);
        if (!Files.exists(path)) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            Object obj = ois.readObject();
            if (obj instanceof Message msg) {
                return msg;
            } else {
                System.out.println("메시지가 아닌 객체 무시: " + obj.getClass().getSimpleName());
                return null;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("메시지 읽기 실패", e);
        }
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        try {
            Files.list(DIRECTORY)
                    .filter(p -> p.toString().endsWith(EXTENSION))
                    .forEach(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            Object obj = ois.readObject();
                            if (obj instanceof Message msg) {
                                messages.add(msg);
                            } else {
                                System.out.println("메시지가 아닌 객체 무시: " + obj.getClass().getSimpleName());
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public Message updateMessage(UUID userUUID, String newMessage) {
        Message msg = find(userUUID);
        if (msg != null) {
            msg.updateMessageIfUser(userUUID, newMessage);
            saveMessage(msg);
        }
        return msg;
    }

    @Override
    public void delete(UUID userUUID) {
        Path path = DIRECTORY.resolve(userUUID + EXTENSION);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("메시지 삭제 실패: " + userUUID, e);
        }
    }
}



