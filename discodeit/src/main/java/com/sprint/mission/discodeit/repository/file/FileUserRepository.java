package com.sprint.mission.discodeit.repository.file;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.io.*;
import java.util.*;
public class FileUserRepository implements UserRepository {

    private final String filePath;
    private Map<UUID, User> userMap;
    public FileUserRepository(String filePath) {
        this.filePath = filePath;
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        this.userMap = loadData();
    }
    @SuppressWarnings("unchecked")
    private Map<UUID, User> loadData() {
        File file = new File(this.filePath);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[FileRepository Error] Failed to load data from " + filePath + ": " + e.getMessage());
            return new HashMap<>();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.filePath))) {
            oos.writeObject(userMap);
        } catch (IOException e) {
            System.err.println("[FileRepository Error] Failed to save data to " + filePath + ": " + e.getMessage());
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
        for (User user : userMap.values()) {
            if (user.getDisplayName().equals(displayName)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        for (User user : userMap.values()) {
            if (user.getEmail().equals(email)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
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