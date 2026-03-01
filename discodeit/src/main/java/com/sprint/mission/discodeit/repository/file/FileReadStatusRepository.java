package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class FileReadStatusRepository implements ReadStatusRepository {

  private final Path DIRECTORY;
  private final String EXTENSION = ".ser";
  private final FileLockProvider fileLockProvider;

  public FileReadStatusRepository(String path, FileLockProvider fileLockProvider) {
    this.DIRECTORY = Paths.get(path);
    try {
      if (Files.notExists(DIRECTORY)) {
        Files.createDirectories(DIRECTORY);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.fileLockProvider = fileLockProvider;
  }

  private Path resolvePath(UUID id) {
    return DIRECTORY.resolve(id + EXTENSION);
  }

  @Override
  public ReadStatus save(ReadStatus readStatus) {
    Path path = resolvePath(readStatus.getId());
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
      oos.writeObject(readStatus);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
    return readStatus;
  }

  @Override
  public Optional<ReadStatus> findById(UUID id) {
    Path path = resolvePath(id);
    if (!Files.exists(path)) {
      return Optional.empty();
    }
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
      return Optional.ofNullable((ReadStatus) ois.readObject());
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    try (Stream<Path> paths = Files.list(DIRECTORY)) {
      return paths.filter(p -> p.toString().endsWith(EXTENSION)).map(p -> {
        ReentrantLock lock = fileLockProvider.getLock(p);
        lock.lock();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(p.toFile()))) {
          return (ReadStatus) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
          throw new RuntimeException(e);
        } finally {
          lock.unlock();
        }
      }).filter(rs -> rs.getUserId().equals(userId)).toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<ReadStatus> findAllByChannelId(UUID channelId) {
    try (Stream<Path> paths = Files.list(DIRECTORY)) {
      return paths.filter(p -> p.toString().endsWith(EXTENSION)).map(p -> {
        ReentrantLock lock = fileLockProvider.getLock(p);
        lock.lock();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(p.toFile()))) {
          return (ReadStatus) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
          throw new RuntimeException(e);
        } finally {
          lock.unlock();
        }
      }).filter(rs -> rs.getChannelId().equals(channelId)).toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean existsById(UUID id) {
    return Files.exists(resolvePath(id));
  }

  @Override
  public void deleteById(UUID id) {
    try {
      Files.deleteIfExists(resolvePath(id));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteAllByChannelId(UUID channelId) {
    findAllByChannelId(channelId).forEach(rs -> deleteById(rs.getId()));
  }

  @Override
  public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
    return findAllByUserId(userId).stream().filter(rs -> rs.getChannelId().equals(channelId))
        .findFirst();
  }
}