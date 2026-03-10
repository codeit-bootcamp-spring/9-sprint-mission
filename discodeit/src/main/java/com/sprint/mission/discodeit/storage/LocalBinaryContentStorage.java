package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;

import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
    this.root = Paths.get(rootPath);
  }

  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new UncheckedIOException("저장소 폴더를 생성할 수 없습니다.", e);
    }
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    Path target = resolvePath(id); // 이제 정상 호출됩니다.
    try {
      Files.write(target, bytes);
      return id;
    } catch (IOException e) {
      throw new UncheckedIOException("파일 저장 실패", e);
    }
  }

  @Override
  public InputStream get(UUID id) {
    try {
      return Files.newInputStream(resolvePath(id));
    } catch (IOException e) {
      throw new UncheckedIOException("파일 읽기 실패", e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto dto) {
    Resource resource = new InputStreamResource(get(dto.id()));
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + dto.fileName() + "\"")
        .contentType(MediaType.parseMediaType(dto.contentType()))
        .body(resource);
  }

  // [추가] 파일 저장 위치 규칙을 정의하는 전용 메서드
  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }
}