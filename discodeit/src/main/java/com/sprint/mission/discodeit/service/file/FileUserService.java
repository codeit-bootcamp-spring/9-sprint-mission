package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

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

    @Override
    public User create(String displayName, String email, String phoneNumber) {
        User user = new User(displayName, email, phoneNumber);
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
    public User update(UUID userId, String displayName, String email, String phoneNumber) {
        User user = readOrThrow(userId);
        user.update(displayName, email, phoneNumber);
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

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) return false;

        /// file 기반 저장소이므로 전체를 순회하며 중복 체크
        return findAll().stream()
                .map(User::getEmail)
                .filter(Objects::nonNull)
                .anyMatch(e -> e.equalsIgnoreCase(email));
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) return false;

        String normalizedTarget = normalizePhoneNumber(phoneNumber);

        return findAll().stream()
                .map(User::getPhoneNumber)
                .filter(Objects::nonNull)
                .map(this::normalizePhoneNumber)
                .anyMatch(p -> p.equals(normalizedTarget));
    }

    private String normalizePhoneNumber(String raw) {
        return raw.replaceAll("\\D", "");
    }

    // 중복제거
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