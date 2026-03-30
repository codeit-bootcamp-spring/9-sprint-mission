package com.sprint.mission.discodeit.repository.file;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public abstract class AbstractFileRepository<T extends Serializable> {

  private final Path directory;
  private final String extension = ".ser";
  private final FileLockProvider fileLockProvider;

  protected AbstractFileRepository(
      String fileDirectory,
      Class<T> clazz,
      FileLockProvider fileLockProvider
  ) {
    this.fileLockProvider = fileLockProvider;
    this.directory = Paths.get(System.getProperty("user.dir"), fileDirectory, clazz.getSimpleName());

    try {
      if (Files.notExists(directory)) {
        Files.createDirectories(directory);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected abstract UUID getId(T entity);

  protected Path resolvePath(UUID id) {
    return directory.resolve(id + extension);
  }

  protected <R> R executeWithLock(Path path, java.util.function.Supplier<R> action) {
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try {
      return action.get();
    } finally {
      lock.unlock();
    }
  }

  protected void executeWithLock(Path path, Runnable action) {
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try {
      action.run();
    } finally {
      lock.unlock();
    }
  }

  public T save(T entity) {
    Path path = resolvePath(getId(entity));
    return executeWithLock(path, () -> {
      try (FileOutputStream fos = new FileOutputStream(path.toFile());
          ObjectOutputStream oos = new ObjectOutputStream(fos)) {
        oos.writeObject(entity);
        return entity;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public Optional<T> findById(UUID id) {
    Path path = resolvePath(id);
    if (!Files.exists(path)) return Optional.empty();

    return executeWithLock(path, () -> {
      try (FileInputStream fis = new FileInputStream(path.toFile());
          ObjectInputStream ois = new ObjectInputStream(fis)) {
        return Optional.of((T) ois.readObject());
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public List<T> findAll() {
    return executeWithLock(directory, () -> {
      try (Stream<Path> paths = Files.list(directory)) {
        return paths
            .filter(p -> p.toString().endsWith(extension))
            .map(p -> {
              try (FileInputStream fis = new FileInputStream(p.toFile());
                  ObjectInputStream ois = new ObjectInputStream(fis)) {
                return (T) ois.readObject();
              } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
              }
            })
            .toList();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public boolean existsById(UUID id) {
    Path path = resolvePath(id);
    return executeWithLock(path, () -> Files.exists(path));
  }

  public void delete(UUID id) {
    executeWithLock(resolvePath(id), () -> {
      try {
        Files.deleteIfExists(resolvePath(id));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public T update(T entity) {
    if (!existsById(getId(entity))) {
      throw new IllegalArgumentException("Entity not found: " + getId(entity));
    }
    return save(entity);
  }
}
