package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.entity.User;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileUserRepository implements UserRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    private final FileLockProvider fileLockProvider;

    public FileUserRepository(
        @Value("${discodeit.repository.file-directory:data}") String fileDirectory,
        FileLockProvider fileLockProvider
    ) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), fileDirectory,
            User.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.fileLockProvider = fileLockProvider;
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public void save(User user) {
        Path path = resolvePath(user.getId());
        ReentrantLock lock = fileLockProvider.getLock(path);
        lock.lock();

        try (
            FileOutputStream fos = new FileOutputStream(path.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(UUID id) {
        Path path = resolvePath(id);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("User with id " + id + " not found");
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public Optional<User> findByID(UUID id) {
        User user = null;
        Path path = resolvePath(id);
        ReentrantLock lock = fileLockProvider.getLock(path);
        lock.lock();
        if (Files.exists(path)) {
            try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                user = (User) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        List<User> userList = findAll();

        UUID targetId = userList.stream()
                .filter(user -> user.getUserName().equals(userName))
                .map(BaseEntity::getId)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("User with username " + userName + " not found"));


        return this.findByID(targetId);
    }

    @Override
    public List<User> findAll() {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            return paths
                .filter(path -> path.toString().endsWith(EXTENSION))
                .map(path -> {
                    ReentrantLock lock = fileLockProvider.getLock(path);
                    lock.lock();
                    try (
                        FileInputStream fis = new FileInputStream(path.toFile());
                        ObjectInputStream ois = new ObjectInputStream(fis)
                    ) {
                        return (User) ois.readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }  finally {
                        lock.unlock();
                    }
                })
                .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean registUser(User user){
        UUID userId = user.getId();
        List<User> userList = findAll();

        for (User u : userList){
            if (u.getUserName().equals(user.getUserName())
                    || u.getEmail().equals(user.getEmail())){
                return false;
            }
        }

        save(user);

        return true;
    }


    @Override
    public boolean withdrawUser(User user){
        remove(user.getId());
        return true;
    }
}
