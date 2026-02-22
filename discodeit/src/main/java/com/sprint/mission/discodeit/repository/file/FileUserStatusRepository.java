package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.AbstractFileRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
    prefix = "discodeit.repository",
    name = "type",
    havingValue = "file"
)
public class FileUserStatusRepository extends AbstractFileRepository<UserStatus> implements
    UserStatusRepository {

  private final Path directory;
  private final FileLockProvider fileLockProvider;

  public FileUserStatusRepository(
      @Value("${discodeit.repository.file-directory:.discodeit}") String baseDir,
      FileLockProvider fileLockProvider
  ) {
    this.fileLockProvider = fileLockProvider;
    this.directory = Paths.get(
        System.getProperty("user.dir"),
        baseDir,
        UserStatus.class.getSimpleName()
    );
    ensureDirectory();
  }

  @Override
  protected Path directory() {
    return directory;
  }

  @Override
  public UserStatus save(UserStatus status) {
    if (status == null) {
      throw new IllegalArgumentException("status is null");
    }
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try {
      write(resolvePath(status.getId()), status);
      return status;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Optional<UserStatus> findById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try {
      Path path = resolvePath(id);
      if (!exists(path)) {
        return Optional.empty();
      }
      return Optional.of(read(path));
    } finally {
      lock.unlock();
    }
  }

  @Override
  public List<UserStatus> findAll() {
    ensureDirectory();
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try {
      List<UserStatus> result = new ArrayList<>();
      try (var paths = Files.list(directory)) {
        paths.forEach(path -> {
          if (Files.isRegularFile(path)) {
            result.add(read(path));
          }
        });
      } catch (IOException e) {
        throw new RuntimeException("Failed to list directory: " + directory, e);
      }
      return result;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void delete(UUID id) {
    if (id == null) {
      return;
    }
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try {
      delete(resolvePath(id));
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean existsById(UUID id) {
    if (id == null) {
      return false;
    }
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try {
      return exists(resolvePath(id));
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    if (userId == null) {
      return Optional.empty();
    }
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try {
      try (var paths = Files.list(directory)) {
        return paths
            .filter(Files::isRegularFile)
            .map(this::read)
            .filter(us -> us != null && userId.equals(us.getUserId()))
            .findFirst();
      } catch (IOException e) {
        throw new RuntimeException("Failed to list directory: " + directory, e);
      }
    } finally {
      lock.unlock();
    }
  }
}
