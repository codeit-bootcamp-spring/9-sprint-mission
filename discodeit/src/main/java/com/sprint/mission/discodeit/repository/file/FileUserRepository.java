package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Repository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.io.*;
import java.util.*;

@Repository
public class FileUserRepository implements UserRepository {
    private final String FILE_PATH = "users.ser";
    private Map<UUID, User> userMap;

    public FileUserRepository() {
        this.userMap = loadData();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, User> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(userMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(User user) {
        userMap.put(user.getId(), user);
        saveData();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public Optional<User> findByDisplayName(String displayName) {
        return userMap.values().stream()
                .filter(u -> u.getDisplayName().equals(displayName))
                .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userMap.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public void delete(UUID id) {
        if (userMap.remove(id) != null) {
            saveData();
        }
    }
}