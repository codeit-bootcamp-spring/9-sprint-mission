package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
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

    private final Map<String, UUID> nameMap;
    private final Map<String, UUID> emailMap;

    public FileUserRepository() {
        this.nameMap = new HashMap<>();
        this.emailMap = new HashMap<>();
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", User.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public void save(User user) {
        Path path = resolvePath(user.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
    public User findByID(UUID id) {
        User userNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                userNullable = (User) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return Optional.ofNullable(userNullable)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
    }

    @Override
    public User findByUserName(String userName) {
        UUID targetId = nameMap.get(userName);

        User userNullable = null;
        Path path = resolvePath(targetId);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                userNullable = (User) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return Optional.ofNullable(userNullable)
                .orElseThrow(() -> new NoSuchElementException("User with id " + targetId + " not found"));
    }

    @Override
    public List<User> findAll() {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
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
        String userName = user.getName();
        String userEmail = user.getEmail();

        UUID nameOwner = nameMap.putIfAbsent(user.getName(), userId);
        if (nameOwner != null) {
            return false;
        }

        UUID emailOwner = emailMap.putIfAbsent(user.getEmail(), user.getId());
        if (emailOwner != null) {
            nameMap.remove(user.getName());
            return false;
        }

        try {
            save(user);
            return true;
        } catch (Exception e){
            emailMap.remove(userEmail);
            nameMap.remove(userName);
            throw e;
        }
    }


    @Override
    public boolean withdrawUser(User user){
        try {
            remove(user.getId());
            nameMap.remove(user.getName());
            emailMap.remove(user.getEmail());
        } catch (Exception e){
            throw e;
        }
        return true;
    }
}
