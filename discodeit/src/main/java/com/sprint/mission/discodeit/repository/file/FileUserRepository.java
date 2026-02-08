package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileUserRepository implements UserRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserRepository() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "file-data-map", User.class.getSimpleName());
            if (Files.notExists(DIRECTORY)) {
                try {
                    Files.createDirectories(DIRECTORY);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        }

    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public User save(User user) {
        Path path = resolvePath(user.getUserId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return user;
    }


    @Override
    public Optional<User> findByUserId(UUID userId) {
        User userNullable = null;
        Path path = resolvePath(userId);
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

        return Optional.ofNullable(Optional.ofNullable(userNullable)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found")));
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        try {
            return findAllUser().stream()
                    .filter(user -> user.getUserName().equals(userName))
                    .findFirst();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<User> findAllUser() {

        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            System.out.println(path);
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
    public boolean existsById(UUID userId) {
        User userNullable = null;
        Path path = resolvePath(userId);
        if (Files.notExists(path)) {
            try(
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ){
                userNullable = (User) ois.readObject();
            }catch (IOException | ClassNotFoundException e){
                throw new RuntimeException(e);
            }
        }
        return userNullable != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        try {
            return findAllUser().stream()
                    .anyMatch(user -> user.getEmail().equals(email));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsByUserName(String userName) {
        try {
            return findAllUser().stream()
                    .anyMatch(user -> user.getUserName().equals(userName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existByProfileId(UUID profileId) {
        try {
            return findAllUser().stream()
                    .anyMatch(user -> user.getProfileId().equals(profileId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(UUID userId) {
        Path path = resolvePath(userId);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        try {
            Files.delete(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
