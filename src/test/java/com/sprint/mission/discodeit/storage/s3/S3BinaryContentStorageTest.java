package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@ExtendWith(MockitoExtension.class)
class S3BinaryContentStorageTest {

  @Mock
  private S3Client s3Client;

  @Mock
  private S3Presigner presigner;

  private S3BinaryContentStorage storage;

  @BeforeEach
  void setUp() {
    storage = new S3BinaryContentStorage(
        "test-bucket",
        600L,
        s3Client,
        presigner
    );
  }

  @Test
  @DisplayName("파일 업로드 성공")
  void put_success() {
    UUID id = UUID.randomUUID();
    byte[] bytes = "test content".getBytes();

    given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .willReturn(PutObjectResponse.builder().build());

    UUID result = storage.put(id, bytes);

    assertThat(result).isEqualTo(id);
    then(s3Client).should().putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }

  @Test
  @DisplayName("다운로드 시 302 리다이렉트 반환")
  void download_returnsRedirect() throws MalformedURLException {
    UUID id = UUID.randomUUID();
    BinaryContentDto dto = new BinaryContentDto(id, "test.txt", 100L, "text/plain");

    PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
    given(presignedRequest.url()).willReturn(new URL("https://test-bucket.s3.amazonaws.com/test"));
    given(presigner.presignGetObject(any(GetObjectPresignRequest.class)))
        .willReturn(presignedRequest);

    ResponseEntity<Void> response = storage.download(dto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    assertThat(response.getHeaders().getLocation()).isNotNull();
  }
}