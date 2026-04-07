package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.s3.AwsProperties;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@SpringBootTest
@ActiveProfiles("test")
public class AWSS3Test {

  @Autowired
  private S3Client s3Client; // S3Config에서 생성된 Bean 주입

  @Autowired
  private AwsProperties props; // @ConfigurationProperties로 로드된 설정 주입

  private S3Presigner s3Presigner;

  @BeforeEach
  void setUp() {
    // Presigned URL 생성을 위한 Presigner 초기화 (S3Config 로직 활용)
    this.s3Presigner = S3Presigner.builder()
        .region(Region.of(props.getRegion()))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(
                props.getAccessKey(),
                props.getSecretKey()
            )
        ))
        .build();
  }

  @Test
  @DisplayName("S3 API 종합 테스트: 업로드, 다운로드, Presigned URL 생성")
  void s3FullProcessTest() {
    String bucketName = props.getBucket();
    String key = "test-mission/hello-s3.txt";
    String content = "Hello Discodeit! S3 Test Successful.";

    // 1. 업로드 테스트
    testUpload(bucketName, key, content);

    // 2. 다운로드 테스트
    testDownload(bucketName, key, content);

    // 3. Presigned URL 생성 테스트
    testCreatePresignedUrl(bucketName, key);
  }

  private void testUpload(String bucket, String key, String content) {
    PutObjectRequest putRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .contentType("text/plain")
        .build();

    s3Client.putObject(putRequest, RequestBody.fromString(content));
  }

  private void testDownload(String bucket, String key, String expectedContent) {
    GetObjectRequest getRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(getRequest);
    String downloadedContent = responseBytes.asString(StandardCharsets.UTF_8);

    assertThat(downloadedContent).isEqualTo(expectedContent);
  }

  private void testCreatePresignedUrl(String bucket, String key) {
    GetObjectRequest getRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10)) // 10분 유효
        .getObjectRequest(getRequest)
        .build();

    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
    String url = presignedRequest.url().toString();
    assertThat(url).contains(bucket).contains(key);
  }
}
