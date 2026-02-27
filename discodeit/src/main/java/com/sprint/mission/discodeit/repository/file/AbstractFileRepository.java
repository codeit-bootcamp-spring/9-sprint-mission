package com.sprint.mission.discodeit.repository.file;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public abstract class AbstractFileRepository<T extends Serializable> {

  private static final String EXT = ".ser";

  protected abstract Path directory();

  protected void ensureDirectory() {
    try {
      Files.createDirectories(directory());
    } catch (IOException e) {
      throw new RuntimeException("Failed to create directory: " + directory(), e);
    }
  }

  protected Path resolvePath(UUID id) {
    return directory().resolve(id.toString() + EXT);
  }

  protected T read(Path path) {
    try (InputStream is = Files.newInputStream(path);
        ObjectInputStream ois = new ObjectInputStream(is)) {
      @SuppressWarnings("unchecked")
      T obj = (T) ois.readObject();
      return obj;
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException("Failed to read: " + path, e);
    }
  }

  protected void write(Path path, T value) {
    ensureDirectory();
    try (OutputStream os = Files.newOutputStream(path);
        ObjectOutputStream oos = new ObjectOutputStream(os)) {
      oos.writeObject(value);
    } catch (IOException e) {
      throw new RuntimeException("Failed to write: " + path, e);
    }
  }

  protected boolean exists(Path path) {
    return Files.exists(path);
  }

  protected void delete(Path path) {
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      throw new RuntimeException("Failed to delete file: " + path, e);
    }
  }
}