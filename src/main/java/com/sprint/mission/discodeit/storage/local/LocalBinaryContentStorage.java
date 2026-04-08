package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 로컬 디스크 파일 저장소 구현체.
 * 설정의 discodeit.storage.type=local 일 때 활성화된다.
 * 파일은 UUID를 파일명으로 사용하여 root-path 하위에 저장된다.
 */
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path}") String rootPath) {
    this.root = Paths.get(rootPath);
  }

  @PostConstruct
  public void init() {
    if (Files.notExists(root)) {
      try {
        Files.createDirectories(root);
      } catch (IOException e) {
        throw new RuntimeException("스토리지 루트 디렉토리 초기화 실패", e);
      }
    }
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    Path filePath = resolvePath(id);
    try {
      Files.write(filePath, bytes);
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 실패: " + id, e);
    }
    return id;
  }

  @Override
  public InputStream get(UUID id) {
    Path filePath = resolvePath(id);
    try {
      return Files.newInputStream(filePath);
    } catch (IOException e) {
      throw new RuntimeException("파일 로드 실패: " + id, e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto metaData) {
    InputStream inputStream = get(metaData.id());
    Resource resource = new InputStreamResource(inputStream);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment()
                .filename(metaData.fileName())
                .build()
                .toString())
        .contentType(MediaType.parseMediaType(
            metaData.contentType() != null
                ? metaData.contentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE))
        .contentLength(metaData.size())
        .body(resource);
  }

  @Override
  public void delete(UUID id) {
    Path filePath = resolvePath(id);
    try {
      Files.deleteIfExists(filePath);
    } catch (IOException e) {
      throw new RuntimeException("파일 삭제 실패: " + id, e);
    }
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }
}
