package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.status.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserStatusRepository() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "file-data-map", UserStatus.class.getSimpleName());
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
    public UserStatus save(UserStatus userStatus) {
        Path path = resolvePath(userStatus.getUserId());
        if (!Files.exists(path)) {
            try (
                    FileOutputStream fos = new FileOutputStream(path.toFile());
                    ObjectOutputStream oos = new ObjectOutputStream(fos)
            ) {
                oos.writeObject(userStatus);
                return userStatus;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        throw new IllegalStateException("UserStatus with id " + userStatus.getUserId() + " already exists");
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        UserStatus userNullable = null;
        Path path = resolvePath(userId);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                userNullable = (UserStatus) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return Optional.ofNullable(Optional.ofNullable(userNullable)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found")));
    }

    @Override
    public Optional<UserStatus> findAllByUserId(UUID userId) {
        UserStatus userNullable = null;
        Path path = resolvePath(userId);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                userNullable = (UserStatus) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return Optional.ofNullable(Optional.ofNullable(userNullable)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found")));
    }



    @Override
    public UserStatus update(UserStatus userStatus) {
        Path path = resolvePath(userStatus.getUserId());
        if (Files.exists(path)) {
            try (
                    FileOutputStream fos = new FileOutputStream(path.toFile());
                    ObjectOutputStream oos = new ObjectOutputStream(fos)
            ) {
                 oos.writeObject(userStatus);
                 return userStatus;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new NoSuchElementException("UserStatus with id " + userStatus.getUserId() + " not found");
        }
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatus userStatus) {
        Path path = resolvePath(userStatus.getUserId());
        if (Files.exists(path)) {
            try (
                    FileOutputStream fos = new FileOutputStream(path.toFile());
                    ObjectOutputStream oos = new ObjectOutputStream(fos)
            ) {
                oos.writeObject(userStatus);
                return userStatus;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new NoSuchElementException("UserStatus with id " + userStatus.getUserId() + " not found");
        }
    }

    @Override
    public boolean existsById(UUID userId) {
        UserStatus userStatus = null;
        Path path = resolvePath(userId);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
               userStatus = (UserStatus) ois.readObject();
            } catch (IOException | ClassNotFoundException e){
                throw new RuntimeException(e);
            }
        } else {
            throw new NoSuchElementException("UserStatus with id " + userStatus.getUserId() + " not found");
        }
        return userStatus != null;
    }

    @Override
    public void deleteById(UUID userId) {
        Path path = resolvePath(userId);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("UserStatus with id " + userId + " not found");
        }
        try {
            Files.delete(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
