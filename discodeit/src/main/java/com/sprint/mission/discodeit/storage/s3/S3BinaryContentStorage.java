package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.s3.AwsProperties;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private static final String BASE_DIR = "images/";

  private final S3Client s3Client;
  private final AwsProperties awsProperties;

  private String accessKey;
  private String secretKey;
  private String region;
  private String bucket;

  public S3BinaryContentStorage(AwsProperties awsProperties, S3Client s3Client) {
    this.awsProperties = awsProperties;
    this.s3Client = s3Client;

    this.accessKey = awsProperties.getAccessKey();
    this.secretKey = awsProperties.getSecretKey();
    this.region = awsProperties.getRegion();
    this.bucket = awsProperties.getBucket();
  }

  @Retryable(
      retryFor = {Exception.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 2000, multiplier = 2.0)
  )
  @Override
  public UUID put(UUID id, byte[] content) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(BASE_DIR + id.toString())
        .build();

    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content));
    return id;
  }

  @Override
  public ResponseEntity<Void> download(BinaryContentDto dto) {
    String presignedUrl = generatePresignedUrl(BASE_DIR + dto.id().toString(), dto.contentType());

    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(presignedUrl))
        .build();
  }

  private S3Client getS3Client() {
    return this.s3Client;
  }

  private String generatePresignedUrl(String key, String contentType) {
    try (S3Presigner presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build()) {

      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .responseContentType(contentType)
          .build();

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(10))
          .getObjectRequest(getObjectRequest)
          .build();

      return presigner.presignGetObject(presignRequest).url().toString();
    }
  }

  @Override
  public InputStream get(UUID id) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(BASE_DIR + id.toString())
        .build();
    return s3Client.getObject(getObjectRequest);
  }

  @Recover
  public UUID recover(Exception e, UUID id, byte[] bytes) {
    String requestId = MDC.get("requestId");
    if (requestId == null) {
      requestId = "N/A (Not Available)";
    }

    String errorMessage = String.format(
        "\n[알림] S3 바이너리 데이터 업로드 최종 실패\n" +
            "작업 이름: S3 Binary Upload\n" +
            "RequestId: %s\n" +
            "BinaryContentId: %s\n" +
            "Error: %s",
        requestId, id, e.getMessage()
    );

    log.error("{}", errorMessage);
    throw new RuntimeException("S3 업로드 최종 실패", e);
  }
}
