package service.file;

import entity.User;
import service.UserService;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileUserService implements UserService {

    private final Path DIRECTORY;
    private static final String EXTENSION = ".ser";

    public FileUserService() {
        this.DIRECTORY = Paths.get(
                System.getProperty("user.dir"),
                "file-data-map",
                User.class.getSimpleName()
        );
        ensureDirectory();
    }

    private void ensureDirectory() {
        try {
            Files.createDirectories(DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + DIRECTORY, e);
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }

    // =========================
    // Public API (과제 시그니처 유지)
    // =========================

    @Override
    public User create(String username, String email, String password) {
        User user = new User(username, email, password);
        write(resolvePath(user.getId()), user);
        return user;
    }

    @Override
    public User findById(UUID userId) {
        return readOrThrow(userId);
    }

    @Override
    public List<User> findAll() {
        ensureDirectory();

        try (var stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(this::readPathAsUser) // Path -> User
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to list directory: " + DIRECTORY, e);
        }
    }

    @Override
    public User update(UUID userId, String newUsername, String newEmail, String newPassword) {
        User user = readOrThrow(userId);
        user.update(newUsername, newEmail, newPassword);
        write(resolvePath(user.getId()), user);
        return user;
    }

    @Override
    public void delete(UUID userId) {
        deleteFileOrThrow(userId);
    }

    @Override
    public boolean existsById(UUID userId) {
        if (userId == null) return false;
        return Files.exists(resolvePath(userId));
    }

    // =========================
    // Helper Methods (중복 제거)
    // =========================

    private User readOrThrow(UUID userId) {
        User user = readOrNull(userId);
        if (user == null) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        return user;
    }

    private User readOrNull(UUID userId) {
        Path path = resolvePath(userId);
        if (Files.notExists(path)) return null;
        return readPathAsUser(path);
    }

    private User readPathAsUser(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            return (User) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read user file: " + path, e);
        }
    }

    private void write(Path path, User user) {
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(user);

        } catch (IOException e) {
            throw new RuntimeException("Failed to write user file: " + path, e);
        }
    }

    private void deleteFileOrThrow(UUID userId) {
        Path path = resolvePath(userId);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete user file: " + path, e);
        }
    }
}