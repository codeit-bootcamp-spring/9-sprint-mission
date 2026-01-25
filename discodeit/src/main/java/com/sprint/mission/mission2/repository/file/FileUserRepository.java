package com.sprint.mission.mission2.repository.file;

import com.sprint.mission.mission2.entity.Message;
import com.sprint.mission.mission2.entity.User;
import com.sprint.mission.mission2.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private static final String userData = "user.dat";

    private Map<UUID, User> load() {
        File file = new File(userData);

        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("불러오기 실패", e);
        }
    }

    @Override
    public void save(User user) {
        Map<UUID, User> users = load();
        users.put(user.getId(), user);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userData))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException("저장 실패", e);
        }
    }

    @Override
    public User read(UUID id) {
        Map<UUID, User> users = load();
        return users.get(id);
    }

    @Override
    public List<User> readAll() {
        Map<UUID, User> users = load();
        return new ArrayList<>(users.values());
    }

    @Override
    public void remove(UUID id) {
        Map<UUID, User> users = load();
        users.remove(id);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userData))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException("저장 실패", e);
        }
    }


}
