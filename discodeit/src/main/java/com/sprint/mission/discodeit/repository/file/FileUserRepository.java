package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private final String FILE_PATH = "users.ser";

    private FileUserRepository() {}
    private static class Holder {
        private static final FileUserRepository INSTANCE = new FileUserRepository();
    }
    public static FileUserRepository getInstance() {
        return Holder.INSTANCE;
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

    // 파일에 전체 목록을 저장하는 내부 메서드 (C++의 fwrite 역할)
    private void saveData(Map<UUID, User> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(User user) {
        Map<UUID, User> data = loadData();
        data.put(user.getId(), user);
        saveData(data);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(loadData().get(id));
    }

    @Override
    public Optional<User> findByDisplayName(String displayName) {
        return loadData().values().stream()
                .filter(u -> u.getDisplayName().equals(displayName))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(loadData().values());
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, User> data = loadData();
        data.remove(id);
        saveData(data);
    }
}