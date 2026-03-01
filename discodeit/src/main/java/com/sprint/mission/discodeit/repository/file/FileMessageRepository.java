package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class FileMessageRepository implements MessageRepository {

  private final Path DIRECTORY;
  private final String EXTENSION = ".ser";
  private final FileLockProvider fileLockProvider;

  public FileMessageRepository(String path, FileLockProvider fileLockProvider) {
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
  public Message save(Message message) {
    Path path = resolvePath(message.getId());
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
      oos.writeObject(message);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
    return message;
  }

  @Override
  public Optional<Message> findById(UUID id) {
    Path path = resolvePath(id);
    if (!Files.exists(path)) {
      return Optional.empty();
    }
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
      return Optional.ofNullable((Message) ois.readObject());
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    try (Stream<Path> paths = Files.list(DIRECTORY)) {
      return paths.filter(p -> p.toString().endsWith(EXTENSION)).map(p -> {
        ReentrantLock lock = fileLockProvider.getLock(p);
        lock.lock();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(p.toFile()))) {
          return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
          throw new RuntimeException(e);
        } finally {
          lock.unlock();
        }
      }).filter(m -> m.getChannelId().equals(channelId)).toList();
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
    findAllByChannelId(channelId).forEach(m -> deleteById(m.getId()));
  }

  @Override
  public Optional<Message> findLatestByChannelId(UUID channelId) {
    return findAllByChannelId(channelId).stream().max(Comparator.comparing(Message::getCreatedAt));
  }
}