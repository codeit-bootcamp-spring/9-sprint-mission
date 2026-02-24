package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class FileUserRepository implements UserRepository {

  private final Path DIRECTORY;
  private final String EXTENSION = ".ser";
  private final FileLockProvider fileLockProvider;

  public FileUserRepository(String path, FileLockProvider fileLockProvider) {
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
  public User save(User user) {
    Path path = resolvePath(user.getId());
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
      oos.writeObject(user);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
    return user;
  }

  @Override
  public Optional<User> findById(UUID id) {
    Path path = resolvePath(id);
    if (!Files.exists(path)) {
      return Optional.empty();
    }
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
      return Optional.ofNullable((User) ois.readObject());
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return findAll().stream().filter(u -> u.getUsername().equals(username)).findFirst();
  }

  @Override
  public boolean existsByEmail(String email) {
    return findAll().stream().anyMatch(u -> u.getEmail().equals(email));
  }

  @Override
  public boolean existsByUsername(String username) {
    return findAll().stream().anyMatch(u -> u.getUsername().equals(username));
  }

  @Override
  public List<User> findAll() {
    try (Stream<Path> paths = Files.list(DIRECTORY)) {
      return paths.filter(p -> p.toString().endsWith(EXTENSION)).map(p -> {
        ReentrantLock lock = fileLockProvider.getLock(p);
        lock.lock();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(p.toFile()))) {
          return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
          throw new RuntimeException(e);
        } finally {
          lock.unlock();
        }
      }).toList();
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
}