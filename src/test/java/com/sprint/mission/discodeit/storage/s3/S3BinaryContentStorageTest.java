package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

class S3BinaryContentStorageTest {

  private S3BinaryContentStorage storage;

  @BeforeEach
  void setUp() {
    storage = new S3BinaryContentStorage(
        "test-access-key",
        "test-secret-key",
        "ap-northeast-2",
        "test-bucket",
        600L
    );
  }

  @Test
  @DisplayName("파일 업로드 성공")
  void put_success() {
    UUID id = UUID.randomUUID();
    byte[] bytes = "test content".getBytes();

    UUID result = storage.put(id, bytes);

    assertThat(result).isEqualTo(id);
  }

  @Test
  @DisplayName("다운로드 시 302 리다이렉트 반환")
  void download_returnsRedirect() {
    BinaryContentDto dto = new BinaryContentDto(
        UUID.randomUUID(),
        Instant.now(),
        "test.txt",
        100L,
        "text/plain"
    );

    ResponseEntity<Void> response = storage.download(dto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    assertThat(response.getHeaders().getLocation()).isNotNull();
  }
}