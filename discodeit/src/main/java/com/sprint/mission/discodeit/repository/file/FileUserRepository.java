package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileUserRepository {

    private final Path storageDir = Paths.get(System.getProperty("user.dir"), "file-data-map", "User");

    public FileUserRepository() {
        try {
            Files.createDirectories(storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(User user) {
        Path file = storageDir.resolve(user.getId() + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User findById(UUID id) {
        Path file = storageDir.resolve(id + ".ser");
        if (!Files.exists(file)) throw new IllegalArgumentException("유저를 찾을 수 없습니다.");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try {
            Files.list(storageDir).forEach(path -> {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    users.add((User) ois.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void delete(UUID id) {
        Path file = storageDir.resolve(id + ".ser");
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
