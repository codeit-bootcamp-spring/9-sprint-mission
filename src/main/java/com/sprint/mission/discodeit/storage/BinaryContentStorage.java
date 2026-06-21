package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface BinaryContentStorage {

  UUID put(UUID binaryContentId, byte[] bytes);

  InputStream get(UUID binaryContentId);

  ResponseEntity<?> download(BinaryContentDto metaData);

  default void delay(int seconds) {
    try {
      Thread.sleep(seconds * 1000L);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread was interrupted", e);
    }
  }
}
