package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(
    prefix = "discodeit.repository",
    name = "type",
    havingValue = "file"
)
public class FileUserRepository extends AbstractFileRepository<User> implements UserRepository {

  private final Path directory;
  private final FileLockProvider fileLockProvider;

  public FileUserRepository(
      @Value("${discodeit.repository.file-directory:.discodeit}") String baseDir,
      FileLockProvider fileLockProvider
  ) {
    this.directory = Paths.get(
        System.getProperty("user.dir"),
        baseDir,
        User.class.getSimpleName()
    );
    this.fileLockProvider = fileLockProvider;
    ensureDirectory();
  }

  @Override
  protected Path directory() {
    return directory;
  }

  @Override
  public User save(User user) {
    if (user == null) {
      throw new IllegalArgumentException("user is null");
    }
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try {
      write(resolvePath(user.getId()), user);
      return user;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Optional<User> findById(UUID userId) {
    if (userId == null) {
      return Optional.empty();
    }
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try {
      Path path = resolvePath(userId);
      if (!exists(path)) {
        return Optional.empty();
      }
      return Optional.of(read(path));
    } finally {
      lock.unlock();
    }
  }

  @Override
  public List<User> findAll() {
    ensureDirectory();
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try (var stream = Files.list(directory)) {
      return stream
          .filter(p -> p.getFileName().toString().endsWith(".ser"))
          .map(this::read)
          .collect(Collectors.toList());
    } catch (Exception e) {
      throw new RuntimeException("Failed to list directory: " + directory, e);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void delete(UUID userId) {
    if (userId == null) {
      return;
    }
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try {
      delete(resolvePath(userId));
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Optional<User> findByEmail(String email) {
    if (email == null || email.isBlank()) {
      return Optional.empty();
    }
    return findAll().stream()
        .filter(u -> u.getEmail() != null)
        .filter(u -> u.getEmail().equalsIgnoreCase(email))
        .findFirst();
  }

  @Override
  public Optional<User> findByUsername(String username) {
    if (username == null || username.isBlank()) {
      return Optional.empty();
    }
    return findAll().stream()
        .filter(u -> u.getUsername() != null)
        .filter(u -> u.getUsername().equalsIgnoreCase(username))
        .findFirst();
  }

  @Override
  public boolean existsById(UUID userId) {
    if (userId == null) {
      return false;
    }
    return exists(resolvePath(userId));
  }

  @Override
  public boolean existsByEmail(String email) {
    if (email == null || email.isBlank()) {
      return false;
    }
    return findAll().stream()
        .map(User::getEmail)
        .filter(Objects::nonNull)
        .anyMatch(e -> e.equalsIgnoreCase(email));
  }

  @Override
  public boolean existsByUsername(String username) {
    if (username == null || username.isBlank()) {
      return false;
    }
    return findAll().stream()
        .map(User::getUsername)
        .filter(Objects::nonNull)
        .anyMatch(username::equalsIgnoreCase);
  }

  @Override
  public boolean existsByPhoneNumber(String phoneNumber) {
    if (phoneNumber == null || phoneNumber.isBlank()) {
      return false;
    }

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