package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface BinaryContentStorage {

  UUID put(UUID id, byte[] data); // 데이터를 S3에 저장

  InputStream get(UUID id);      // 데이터를 S3에서 가져옴

  ResponseEntity<?> download(BinaryContentDto dto); // 리다이렉트를 위한 Presigned URL 생성
}
