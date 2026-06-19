package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Component
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;
  private final long uploadDelayMillis;

  public LocalBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path}") Path root,
      @Value("${discodeit.storage.local.upload-delay-millis:0}") long uploadDelayMillis
  ) {
    this.root = root.toAbsolutePath().normalize();
    this.uploadDelayMillis = uploadDelayMillis;
  }

  @PostConstruct
  public void init() {
    try {
      if (Files.exists(root) && !Files.isDirectory(root)) {
        throw new DiscodeitException(ErrorCode.INTERNAL_SERVER_ERROR,
            Map.of("rootPath", root.toString()));
      }
      if (Files.notExists(root)) {
        Files.createDirectories(root);
      }
      log.info("Local binary content storage initialized: rootPath={}", root);
    } catch (IOException e) {
      throw new DiscodeitException(ErrorCode.INTERNAL_SERVER_ERROR,
          Map.of("rootPath", root.toString()));
    }
  }

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    simulateUploadDelay();

    Path filePath = resolvePath(binaryContentId);
    if (Files.exists(filePath)) {
      throw new DiscodeitException(ErrorCode.BINARY_CONTENT_ALREADY_EXISTS,
          Map.of("binaryContentId", binaryContentId));
    }
    try (OutputStream outputStream = Files.newOutputStream(filePath)) {
      outputStream.write(bytes);
    } catch (IOException e) {
      throw new DiscodeitException(ErrorCode.INTERNAL_SERVER_ERROR,
          Map.of("binaryContentId", binaryContentId));
    }
    return binaryContentId;
  }

  private void simulateUploadDelay() {
    if (uploadDelayMillis <= 0) {
      return;
    }
    try {
      Thread.sleep(uploadDelayMillis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread interrupted while simulating delay", e);
    }
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    Path filePath = resolvePath(binaryContentId);
    if (Files.notExists(filePath)) {
      throw new DiscodeitException(ErrorCode.BINARY_CONTENT_NOT_FOUND,
          Map.of("binaryContentId", binaryContentId));
    }
    try {
      return Files.newInputStream(filePath);
    } catch (IOException e) {
      throw new DiscodeitException(ErrorCode.INTERNAL_SERVER_ERROR,
          Map.of("binaryContentId", binaryContentId));
    }
  }

  private Path resolvePath(UUID key) {
    return root.resolve(key.toString());
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentResponse metaData) {
    InputStream inputStream = get(metaData.id());
    Resource resource = new InputStreamResource(inputStream);

    return ResponseEntity
        .status(HttpStatus.OK)
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + metaData.fileName() + "\"")
        .header(HttpHeaders.CONTENT_TYPE, metaData.contentType())
        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(metaData.size()))
        .body(resource);
  }
}
