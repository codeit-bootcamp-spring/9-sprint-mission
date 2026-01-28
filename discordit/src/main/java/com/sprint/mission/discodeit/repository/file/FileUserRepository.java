package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private final String fileName = "users.dat";

    @Override
    public User save(User user) {
        List<User> users = findAll();
        users.removeIf(u -> u.getId().equals(user.getId()));
        users.add(user);

        saveAllToFile(users);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return findAll().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> findAll() {
        File file = new File(fileName);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteById(UUID id) {
        List<User> users = findAll();
        if (users.removeIf(user -> user.getId().equals(id))) {
            saveAllToFile(users);
        }
    }

    // 파일 저장을 위한 헬퍼 메서드
    private void saveAllToFile(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}