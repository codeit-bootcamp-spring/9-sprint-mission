package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3BinaryContentStorageTest {

  private S3BinaryContentStorage s3Storage;

  @Mock
  private S3Client s3Client;

  @BeforeEach
  void setUp() {
    // 1. 실제 객체 생성
    S3BinaryContentStorage originalStorage = new S3BinaryContentStorage(
        "test-access-key", "test-secret-key", "ap-northeast-2", "test-bucket", 600
    );

    // 2. Spy 객체로 감싸기 (특정 메서드만 가로채기 위해)
    s3Storage = Mockito.spy(originalStorage);
  }

  @Test
  @DisplayName("S3에 파일을 성공적으로 업로드한다 (put)")
  void testPut() {
    // given
    UUID id = UUID.randomUUID();
    byte[] bytes = "hello s3".getBytes();

    // 💡 getS3Client()가 호출되면 우리가 만든 가짜 s3Client를 리턴해라!
    // put 메서드의 try-with-resources 때문에 doReturn을 써야 안전합니다.
    doReturn(s3Client).when(s3Storage).getS3Client();

    when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .thenReturn(null);

    // when
    UUID resultId = s3Storage.put(id, bytes);

    // then
    assertThat(resultId).isEqualTo(id);
    verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }

  @Test
  @DisplayName("S3에서 파일을 성공적으로 가져온다 (get)")
  void testGet() {
    // given
    UUID id = UUID.randomUUID();
    byte[] expectedBytes = "hello s3".getBytes();

    ResponseInputStream<GetObjectResponse> mockInputStream =
        new ResponseInputStream<>(GetObjectResponse.builder().build(), new ByteArrayInputStream(expectedBytes));

    // 💡 getS3Client() 가로채기
    doReturn(s3Client).when(s3Storage).getS3Client();
    when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockInputStream);

    // when
    InputStream resultStream = s3Storage.get(id);

    // then
    assertThat(resultStream).isNotNull();
    verify(s3Client, times(1)).getObject(any(GetObjectRequest.class));
  }

  @Test
  @DisplayName("Presigned URL을 생성하여 302 리다이렉트 응답을 반환한다 (download)")
  void testDownload() {
    // given
    UUID id = UUID.randomUUID();
    BinaryContentDto dto = new BinaryContentDto(id, "test.png", 1024L, "image/png");
    String fakePresignedUrl = "https://fake-s3-url.com/test.png";

    // 💡 이 메서드는 s3Client를 안 쓰고 내부에서 직접 Presigner를 만드므로
    // 메서드 자체를 가로채는 것이 가장 깔끔합니다.
    doReturn(fakePresignedUrl).when(s3Storage).generatePresignedUrl(anyString(), anyString());

    // when
    ResponseEntity<?> response = s3Storage.download(dto);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(fakePresignedUrl));
  }
}