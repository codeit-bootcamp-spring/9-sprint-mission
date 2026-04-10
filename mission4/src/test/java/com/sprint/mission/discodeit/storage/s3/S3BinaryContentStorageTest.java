package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import software.amazon.awssdk.core.sync.RequestBody;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;


@ExtendWith(MockitoExtension.class)
class S3BinaryContentStorageTest {

  @Mock
  private S3Client s3Client;

  @Mock
  private S3Presigner s3Presigner;

  private S3BinaryContentStorage storage;

  @BeforeEach
  void setup() {

    S3BinaryContentStorage realStorage = new S3BinaryContentStorage("access", "secret",
        "ap-northeast-2", "bucket");
    storage = Mockito.spy(realStorage);
  }

  @Test
  @DisplayName("파일 저장(put) 시 s3Client의 putObject가 호출되어야 한다")
  void put_Success() {

    UUID id = UUID.randomUUID();
    byte[] content = "hello".getBytes();
    doReturn(s3Client).when(storage).getS3client();

    storage.put(id, content);

    verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }

  @Test
  @DisplayName("다운로드 시 리다이렉트 응답을 반환해야 한다")
  void download_Success() {
    BinaryContentDto dto = new BinaryContentDto(UUID.randomUUID(), "test.png", 100L, "image/png");
    doReturn("https://fake-s3-url.com").when(storage).generatePresignedUrl(any(), any());

    ResponseEntity<?> response = storage.download(dto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    assertThat(response.getHeaders().getLocation().toString()).isEqualTo("https://fake-s3-url.com");
  }
}