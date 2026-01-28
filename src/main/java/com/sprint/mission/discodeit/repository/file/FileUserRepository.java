package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

@Repository
public class FileUserRepository implements UserRepository {

    private final File file;
    private final Map<UUID, User> data;

    public FileUserRepository() {
        Path directory = Path.of(System.getProperty("user.dir"), "file-data", "user");
        File dir = directory.toFile();

        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new RuntimeException("유저 디렉토리 생성 실패: " + dir.getAbsolutePath());
            }
        }

        this.file = directory.resolve("users.ser").toFile();
        this.data = load();
    }


    @Override
    public User save(User user) {
        data.put(user.getId(), user);
        persist();
        return user;
    }

    @Override
    public User findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(User user) {
        data.put(user.getId(), user);
        persist();
        return user;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        persist();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return data.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    // 파일 입출력 관련 메서드
    @SuppressWarnings("unchecked")
    private Map<UUID, User> load() {
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {

            return (Map<UUID, User>) ois.readObject();

        } catch (Exception e) {
            throw new RuntimeException("유저 파일 로딩 실패", e);
        }
    }

    private void persist() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(file))) {

            oos.writeObject(data);

        } catch (IOException e) {
            throw new RuntimeException("유저 파일 저장 실패", e);
        }
    }
}
