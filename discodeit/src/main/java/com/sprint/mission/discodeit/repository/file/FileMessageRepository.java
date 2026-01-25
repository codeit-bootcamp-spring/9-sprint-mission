package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileMessageRepository {

    private final Path storageDir;

    public FileMessageRepository() {
        this.storageDir = Paths.get(System.getProperty("user.dir"), "file-data-map", "Message");
        try {
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("메시지 저장 폴더 생성 실패", e);
        }
    }

    // 메시지 저장
    public Message save(Message message) {
        Path file = storageDir.resolve(message.getUser() + ".ser"); // user UUID 기준 저장
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    // 메시지 단건 조회
    public Message find(UUID userUUID) {
        Path file = storageDir.resolve(userUUID + ".ser");
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("메시지가 존재하지 않습니다: " + userUUID);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("메시지 읽기 실패", e);
        }
    }

    // 메시지 전체 조회
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        try {
            Files.list(storageDir).forEach(path -> {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    messages.add((Message) ois.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }

    // 메시지 삭제
    public boolean delete(UUID userUUID) {
        Path file = storageDir.resolve(userUUID + ".ser");
        try {
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
