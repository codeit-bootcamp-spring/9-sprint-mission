package com.sprint.mission.discodeit.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path rootLocation;


  public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String path) {
    this.rootLocation = Paths.get(path);
    try {

      Files.createDirectories(rootLocation);
    } catch (IOException e) {
      throw new RuntimeException("Could not initialize storage", e);
    }
  }

  @Override
  public void put(UUID id, byte[] bytes) {
    try {
      Files.write(rootLocation.resolve(id.toString()), bytes);
    } catch (IOException e) {
      throw new RuntimeException("Could not store file", e);
    }
  }

  @Override
  public Resource loadAsResource(UUID id) {
    try {
      Path file = rootLocation.resolve(id.toString());
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new RuntimeException("Could not read file: " + id);
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException("Could not read file: " + id, e);
    }
  }

  @Override
  public void delete(UUID id) {
    try {
      Files.deleteIfExists(rootLocation.resolve(id.toString()));
    } catch (IOException e) {
      throw new RuntimeException("Could not delete file", e);
    }
  }
}