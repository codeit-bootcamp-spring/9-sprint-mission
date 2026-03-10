package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  @Value("${discodeit.storage.local.root-path}")
  private Path root;

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }

  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new RuntimeException("Failed to create storage directory", e);
    }
  }

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    try {
      Path path = resolvePath(binaryContentId);
      Files.write(path, bytes);
      return binaryContentId;
    } catch (Exception e) {
      throw new RuntimeException("Failed to save file", e);
    }
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    try {
      Path path = resolvePath(binaryContentId);
      return Files.newInputStream(path);
    } catch (Exception e) {
      throw new RuntimeException("Failed to load file", e);
    }
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto metaData) {
    try {
      InputStream inputStream = get(metaData.id());
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metaData.fileName() + "\"")
          .contentType(MediaType.parseMediaType(metaData.contentType()))
          .body(inputStream.readAllBytes());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("File download failed");
    }
  }
}