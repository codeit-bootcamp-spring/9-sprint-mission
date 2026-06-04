package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.storage.BinaryContentUploadFailureNotifier;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

class S3BinaryContentStorageTest {

  private S3Client s3Client;
  private S3Presigner s3Presigner;
  private BinaryContentUploadFailureNotifier failureNotifier;
  private S3BinaryContentStorage storage;

  private static final String BUCKET = "test-bucket";
  private static final long EXPIRATION = 600L;

  @BeforeEach
  void setUp() {
    s3Client = mock(S3Client.class);
    s3Presigner = mock(S3Presigner.class);
    failureNotifier = mock(BinaryContentUploadFailureNotifier.class);
    storage = new S3BinaryContentStorage(s3Client, s3Presigner, failureNotifier, BUCKET, EXPIRATION);
  }

  @Test
  @DisplayName("바이트 배열 업로드 시 binaryContentId를 반환한다")
  void put_returnsId() {
    UUID id = UUID.randomUUID();
    byte[] content = "test content".getBytes();
    when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .thenReturn(PutObjectResponse.builder().build());

    UUID result = storage.put(id, content);

    assertThat(result).isEqualTo(id);
    verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }

  @Test
  @DisplayName("recover 성공: 재시도 최종 실패 정보를 관리자에게 알리고 예외를 다시 던진다")
  void recover_notifiesAdminsAndRethrows() {
    UUID id = UUID.randomUUID();
    byte[] content = "test content".getBytes();
    RuntimeException cause = new RuntimeException("storage error");

    assertThatThrownBy(() -> storage.recover(cause, id, content))
        .isSameAs(cause);

    verify(failureNotifier).notifyAdmins(id, cause);
  }

  @Test
  @DisplayName("파일이 존재하면 InputStream을 반환한다")
  void get_returnsInputStream() {
    UUID id = UUID.randomUUID();
    ResponseInputStream<GetObjectResponse> mockStream = mock(ResponseInputStream.class);
    when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockStream);

    InputStream result = storage.get(id);

    assertThat(result).isEqualTo(mockStream);
    verify(s3Client).getObject(any(GetObjectRequest.class));
  }

  @Test
  @DisplayName("파일이 없으면 DiscodeitException이 발생한다")
  void get_throwsDiscodeitExceptionWhenFileNotFound() {
    UUID id = UUID.randomUUID();
    when(s3Client.getObject(any(GetObjectRequest.class)))
        .thenThrow(NoSuchKeyException.class);

    assertThatThrownBy(() -> storage.get(id))
        .isInstanceOf(DiscodeitException.class);
  }

  @Test
  @DisplayName("presigned URL 생성 시 302 응답과 Location 헤더를 반환한다")
  void download_returns302ResponseWithLocationHeader() throws MalformedURLException {
    UUID id = UUID.randomUUID();
    BinaryContentResponse metaData = new BinaryContentResponse(id, "file.txt", 100L, "text/plain");
    String expectedUrl = "https://" + BUCKET + ".s3.amazonaws.com/" + id
        + "?X-Amz-Algorithm=AWS4-HMAC-SHA256";

    PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
    when(presignedRequest.url()).thenReturn(new URL(expectedUrl));
    when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
        .thenReturn(presignedRequest);

    ResponseEntity<?> response = storage.download(metaData);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    assertThat(response.getHeaders().getLocation()).isNotNull();
    assertThat(response.getHeaders().getLocation().toString()).isEqualTo(expectedUrl);
  }

  @Test
  @DisplayName("download 호출 시 s3Presigner.presignGetObject를 사용한다")
  void download_usesS3PresignerPresignGetObject() throws MalformedURLException {
    UUID id = UUID.randomUUID();
    BinaryContentResponse metaData = new BinaryContentResponse(id, "image.png", 2048L, "image/png");
    String expectedUrl = "https://" + BUCKET + ".s3.amazonaws.com/" + id;

    PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
    when(presignedRequest.url()).thenReturn(new URL(expectedUrl));
    when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
        .thenReturn(presignedRequest);

    storage.download(metaData);

    verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
  }
}
