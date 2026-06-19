package com.sprint.mission.discodeit.storage.local;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LocalBinaryContentStorageTest {

  @TempDir
  Path tempDir;

  @Test
  @DisplayName("init: 로컬 저장 경로를 정규화한 뒤 파일을 저장한다")
  void init_normalizesRootPath() {
    Path configuredRoot = tempDir.resolve("nested").resolve("..").resolve("storage");
    LocalBinaryContentStorage storage = new LocalBinaryContentStorage(configuredRoot, 0);
    UUID binaryContentId = UUID.randomUUID();
    byte[] bytes = new byte[]{1, 2, 3};

    storage.init();
    storage.put(binaryContentId, bytes);

    Path expectedPath = tempDir.resolve("storage").resolve(binaryContentId.toString());
    assertThat(Files.exists(expectedPath)).isTrue();
  }
}
