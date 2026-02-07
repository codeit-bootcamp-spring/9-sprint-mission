package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserRepository implements UserRepository {

    private static final String FILE_NAME = "users.dat";
    private final File baseDir;
    private List<User> users;

    public FileUserRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}") String directory
    ) {
        this.baseDir = new File(directory);
        this.users = load();
    }

    @Override
    public User save(User user) {
        users.add(user);
        persist();
        return user;
    }

    @Override
    public User find(UUID id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User findByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public void delete(UUID id) {
        users.removeIf(user -> user.getId().equals(id));
        persist();
    }

    private void persist() {
        ensureDirectory();
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(resolveFile()))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<User> load() {
        File file = resolveFile();
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                List<?> list = (List<?>) obj;
                List<User> result = new ArrayList<>();
                for (Object o : list) {
                    if (o instanceof User) {
                        result.add((User) o);
                    }
                }
                return result;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private File resolveFile() {
        return new File(baseDir, FILE_NAME);
    }

    private void ensureDirectory() {
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return users.stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}
