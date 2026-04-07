package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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
  private S3BinaryContentStorage storage;

  private static final String BUCKET = "test-bucket";
  private static final long EXPIRATION = 600L;

  @BeforeEach
  void setUp() {
    s3Client = mock(S3Client.class);
    s3Presigner = mock(S3Presigner.class);
    storage = new S3BinaryContentStorage(s3Client, s3Presigner, BUCKET, EXPIRATION);
  }

  @Test
  void put_파일업로드_ID반환() {
    UUID id = UUID.randomUUID();
    byte[] content = "test content".getBytes();
    when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .thenReturn(PutObjectResponse.builder().build());

    UUID result = storage.put(id, content);

    assertThat(result).isEqualTo(id);
    verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }

  @Test
  void get_파일존재_InputStream반환() {
    UUID id = UUID.randomUUID();
    ResponseInputStream<GetObjectResponse> mockStream = mock(ResponseInputStream.class);
    when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockStream);

    InputStream result = storage.get(id);

    assertThat(result).isEqualTo(mockStream);
    verify(s3Client).getObject(any(GetObjectRequest.class));
  }

  @Test
  void get_파일없음_DiscodeitException발생() {
    UUID id = UUID.randomUUID();
    when(s3Client.getObject(any(GetObjectRequest.class)))
        .thenThrow(NoSuchKeyException.class);

    assertThatThrownBy(() -> storage.get(id))
        .isInstanceOf(DiscodeitException.class);
  }

  @Test
  void download_302응답_presignedUrl_Location헤더반환() throws MalformedURLException {
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
  void download_만료시간_presigner_호출확인() throws MalformedURLException {
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
