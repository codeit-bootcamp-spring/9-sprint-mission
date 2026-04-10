package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ExtendWith(MockitoExtension.class)
class S3BinaryContentStorageTest {

  @Mock
  private S3Client s3Client;

  @Mock
  private ResponseInputStream<GetObjectResponse> responseInputStream;

  private S3BinaryContentStorage storage;
  private final S3Properties properties = createProperties();

  @BeforeEach
  void setUp() {
    storage = new S3BinaryContentStorage(s3Client, properties);
  }

  private static S3Properties createProperties() {
    S3Properties properties = new S3Properties();
    properties.setBucket("test-bucket");
    properties.setRegion("us-east-1");
    properties.setAccessKey("dummy-access");
    properties.setSecretKey("dummy-secret");
    properties.setPresignedUrlExpiration(600);
    return properties;
  }

  @Test
  @DisplayName("S3 업로드 요청 시 bucket/key/contentType을 포함해 putObject를 호출한다")
  void put_Success() {
    UUID id = UUID.randomUUID();
    byte[] content = "hello".getBytes();

    UUID result = storage.put(id, content);

    ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
    verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));

    PutObjectRequest request = requestCaptor.getValue();
    assertThat(result).isEqualTo(id);
    assertThat(request.bucket()).isEqualTo("test-bucket");
    assertThat(request.key()).isEqualTo(id.toString());
    assertThat(request.contentType()).isEqualTo("application/octet-stream");
  }

  @Test
  @DisplayName("S3 다운로드 요청 시 getObject를 호출하고 반환 스트림을 그대로 반환한다")
  void get_Success() {
    UUID id = UUID.randomUUID();
    given(s3Client.getObject(any(GetObjectRequest.class))).willReturn(responseInputStream);

    InputStream result = storage.get(id);

    ArgumentCaptor<GetObjectRequest> requestCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
    verify(s3Client).getObject(requestCaptor.capture());

    GetObjectRequest request = requestCaptor.getValue();
    assertThat(result).isSameAs(responseInputStream);
    assertThat(request.bucket()).isEqualTo("test-bucket");
    assertThat(request.key()).isEqualTo(id.toString());
  }

  @Test
  @DisplayName("다운로드 응답은 Presigned URL로 302 리다이렉트를 반환한다")
  void download_Success() {
    UUID id = UUID.randomUUID();
    BinaryContentDto dto = new BinaryContentDto(id, "file.txt", 5L, "text/plain");

    ResponseEntity<Void> response = storage.download(dto);

    String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);
    assertThat(response.getStatusCode().value()).isEqualTo(302);
    assertThat(location).isNotBlank();
    assertThat(location).contains(id.toString());
  }
}

