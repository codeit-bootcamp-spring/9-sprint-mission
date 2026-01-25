package repository.file;

import entity.User;
import repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {

    private final File file = new File("users.dat");

    private Map<UUID, User> load() {
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void saveFile(Map<UUID, User> data) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User addUser(String displayName, String email, String phoneNumber) {
        Map<UUID, User> data = load();
        User user = new User(displayName, email, phoneNumber);
        data.put(user.getId(), user);
        saveFile(data);

        return user;
    }

    @Override
    public Optional<User> getUser(UUID id) {
        return Optional.ofNullable(load().get(id));
    }

    @Override
    public List<User> getAllUser() {
        return new ArrayList<>(load().values());
    }

    @Override
    public void deleteUser(UUID id) {
        Map<UUID, User> data = load();
        data.remove(id);
        saveFile(data);
    }

    @Override
    public boolean existsByDisplayName(String displayName) {
        return load().values().stream()
                .anyMatch(u -> u.getDisplayName().equals(displayName));
    }
}