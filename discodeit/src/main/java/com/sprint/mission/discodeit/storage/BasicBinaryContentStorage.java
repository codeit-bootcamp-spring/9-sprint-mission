package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Primary
@Component
public class BasicBinaryContentStorage implements BinaryContentStorage {

  // 실제 파일 데이터(byte[])를 저장할 메모리 공간입니다.
  private final Map<UUID, byte[]> storage = new ConcurrentHashMap<>();

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    storage.put(binaryContentId, bytes); // 창고에 넣기
    return binaryContentId;
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    byte[] bytes = storage.get(binaryContentId);
    if (bytes == null) {
      // 비어있는 0바이트 배열이라도 넣어줘서 에러를 막습니다.
      bytes = new byte[0];
    }

    return new ByteArrayInputStream(bytes);
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto metaData) {
    InputStream inputStream = this.get(metaData.id());
    Resource resource = new InputStreamResource(inputStream);

    String contentDisposition = "attachment; filename=\"" + metaData.fileName() + "\"";

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
        .contentType(MediaType.parseMediaType(metaData.contentType()))
        .body(resource);
  }
}