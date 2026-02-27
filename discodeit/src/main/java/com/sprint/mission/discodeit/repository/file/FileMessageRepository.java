package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(
    prefix = "discodeit.repository",
    name = "type",
    havingValue = "file"
)
public class FileMessageRepository extends AbstractFileRepository<Message> implements
    MessageRepository {

  private final Path directory;
  private final FileLockProvider fileLockProvider;

  public FileMessageRepository(
      @Value("${discodeit.repository.file-directory:.discodeit}") String baseDir,
      FileLockProvider fileLockProvider
  ) {
    this.fileLockProvider = fileLockProvider;
    this.directory = Paths.get(
        System.getProperty("user.dir"),
        baseDir,
        Message.class.getSimpleName()
    );
    ensureDirectory();
  }

  @Override
  protected Path directory() {
    return directory;
  }

  @Override
  public Message save(Message message) {
    if (message == null) {
      throw new IllegalArgumentException("message is null");
    }
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try {
      write(resolvePath(message.getId()), message);
      return message;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Optional<Message> findById(UUID messageId) {
    if (messageId == null) {
      return Optional.empty();
    }
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try {
      Path path = resolvePath(messageId);
      if (!exists(path)) {
        return Optional.empty();
      }
      return Optional.of(read(path));
    } finally {
      lock.unlock();
    }
  }

  @Override
  public List<Message> findAll() {
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
  public List<Message> findAllByChannelId(UUID channelId) {
    if (channelId == null) {
      return List.of();
    }
    return findAll().stream()
        .filter(m -> channelId.equals(m.getChannelId()))
        .collect(Collectors.toList());
  }

  @Override
  public void delete(UUID messageId) {
    if (messageId == null) {
      return;
    }
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try {
      delete(resolvePath(messageId));
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean existsById(UUID messageId) {
    if (messageId == null) {
      return false;
    }
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try {
      return exists(resolvePath(messageId));
    } finally {
      lock.unlock();
    }
  }
}