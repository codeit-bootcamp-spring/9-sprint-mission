package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.AbstractFileRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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
public class FileChannelRepository extends AbstractFileRepository<Channel> implements
    ChannelRepository {

  private final Path directory;
  private final FileLockProvider fileLockProvider;

  public FileChannelRepository(
      @Value("${discodeit.repository.file-directory:.discodeit}") String baseDir,
      FileLockProvider fileLockProvider
  ) {
    this.fileLockProvider = fileLockProvider;
    this.directory = Paths.get(
        System.getProperty("user.dir"),
        baseDir,
        Channel.class.getSimpleName()
    );
    ensureDirectory();
  }

  @Override
  protected Path directory() {
    return directory;
  }

  @Override
  public Channel save(Channel channel) {
    if (channel == null) {
      throw new IllegalArgumentException("channel is null");
    }
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try {
      write(resolvePath(channel.getId()), channel);
      return channel;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Optional<Channel> findById(UUID channelId) {
    if (channelId == null) {
      return Optional.empty();
    }
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try {
      Path path = resolvePath(channelId);
      if (!exists(path)) {
        return Optional.empty();
      }
      return Optional.of(read(path));
    } finally {
      lock.unlock();
    }
  }

  @Override
  public List<Channel> findAll() {
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
  public void delete(UUID channelId) {
    if (channelId == null) {
      return;
    }
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try {
      delete(resolvePath(channelId));
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean existsById(UUID channelId) {
    if (channelId == null) {
      return false;
    }
    var lock = fileLockProvider.getLock(directory);
    lock.lock();
    try {
      return exists(resolvePath(channelId));
    } finally {
      lock.unlock();
    }
  }
}
