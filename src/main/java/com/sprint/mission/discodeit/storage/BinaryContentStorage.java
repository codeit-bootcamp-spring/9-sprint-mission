package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

/**
 * 파일 저장소 추상화 인터페이스.
 * 구현체에 따라 로컬 디스크, 클라우드 스토리지 등으로 전환 가능하다.
 */
public interface BinaryContentStorage {

  UUID put(UUID id, byte[] bytes);

  InputStream get(UUID binaryContentId);

  ResponseEntity<?> download(BinaryContentDto metaData);

  void delete(UUID id);
}
