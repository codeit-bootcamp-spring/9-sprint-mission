package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.AbstractFileRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileUserRepository extends AbstractFileRepository<User> implements UserRepository {

    private final Path directory;

    public FileUserRepository() {
        this.directory = Paths.get(
                System.getProperty("user.dir"),
                "file-data-map",
                User.class.getSimpleName()
        );
        ensureDirectory();
    }

    @Override
    protected Path directory() {
        return directory;
    }

    @Override
    public User save(User user) {
        if (user == null) throw new IllegalArgumentException("user is null");
        write(resolvePath(user.getId()), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        if (userId == null) return Optional.empty();
        Path path = resolvePath(userId);
        if (!exists(path)) return Optional.empty();
        return Optional.of(read(path));
    }

    @Override
    public List<User> findAll() {
        ensureDirectory();
        try (var stream = Files.list(directory)) {
            return stream
                    .filter(p -> p.getFileName().toString().endsWith(".ser"))
                    .map(this::read)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to list directory: " + directory, e);
        }
    }

    @Override
    public void delete(UUID userId) {
        if (userId == null) return;
        delete(resolvePath(userId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) return Optional.empty();
        return findAll().stream()
                .filter(u -> u.getEmail() != null)
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public Optional<User> findByUsername(String displayName) {
        if (displayName == null || displayName.isBlank()) return Optional.empty();
        return findAll().stream()
                .filter(u -> displayName.equals(u.getDisplayName()))
                .findFirst();
    }

    @Override
    public boolean existsById(UUID userId) {
        if (userId == null) return false;
        return exists(resolvePath(userId));
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return findAll().stream()
                .map(User::getEmail)
                .filter(Objects::nonNull)
                .anyMatch(e -> e.equalsIgnoreCase(email));
    }

    @Override
    public boolean existsByUsername(String displayName) {
        if (displayName == null || displayName.isBlank()) return false;
        return findAll().stream()
                .map(User::getDisplayName)
                .filter(Objects::nonNull)
                .anyMatch(displayName::equals);
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

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) return Optional.empty();
        return findAll().stream()
                .filter(u -> u.getUsername() != null)
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    private String normalizePhoneNumber(String raw) {
        return raw.replaceAll("\\D", "");
    }
}