package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface BinaryContentStorage {

  // 반환 타입을 void에서 UUID로 변경
  UUID put(UUID id, byte[] bytes);

  InputStream get(UUID id);

  ResponseEntity<Resource> download(BinaryContentDto dto);
}