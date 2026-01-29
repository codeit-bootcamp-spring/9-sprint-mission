package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.AbstractFileRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Objects;

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
    public void deleteById(UUID userId) {
        if (userId == null) return;
        delete(resolvePath(userId));
    }

    @Override
    public boolean existsById(UUID userId) {
        if (userId == null) return false;
        return exists(resolvePath(userId));
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
}