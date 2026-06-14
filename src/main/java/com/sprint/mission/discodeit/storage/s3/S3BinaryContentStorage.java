package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.transaction.annotation.Transactional;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@Component
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucket;

  @Value("${discodeit.storage.s3.presigned-url-expiration:600}") // 기본값 10분
  private long presignedUrlExpirationSeconds;

  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final ApplicationEventPublisher eventPublisher;

  public S3BinaryContentStorage(
      @Value("${discodeit.storage.s3.access-key}") String accessKey,
      @Value("${discodeit.storage.s3.secret-key}") String secretKey,
      @Value("${discodeit.storage.s3.region}") String region,
      @Value("${discodeit.storage.s3.bucket}") String bucket,
      UserRepository userRepository,
      NotificationRepository notificationRepository,
      ApplicationEventPublisher eventPublisher
  ) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
    this.bucket = bucket;
    this.userRepository = userRepository;
    this.notificationRepository = notificationRepository;
    this.eventPublisher = eventPublisher;
  }

  @Retryable(value = {RuntimeException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    String key = binaryContentId.toString();
    try {
      S3Client s3Client = getS3Client();

      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .build();

      s3Client.putObject(request, RequestBody.fromBytes(bytes));
      log.info("S3에 파일 업로드 성공: {}", key);

      return binaryContentId;
    } catch (S3Exception e) {
      log.error("S3에 파일 업로드 실패: {}", e.getMessage());
      throw new RuntimeException("S3에 파일 업로드 실패: " + key, e);
    }
  }

  @Recover
  @Transactional
  public UUID recover(RuntimeException e, UUID binaryContentId, byte[] bytes) {
    // 모든 재시도 실패 시 이벤트 발행 (Kafka로 전달되도록)
    log.error("S3 업로드 재시도 모두 실패: id={}, error={}", binaryContentId, e.getMessage(), e);

    String requestId = MDC.get("requestId");
    if (requestId == null) {
      requestId = MDC.get("request-id");
    }

    StringBuilder content = new StringBuilder();
    content.append("Task: BinaryContentStorage.put (S3 upload)").append('\n');
    content.append("RequestId: ");
    content.append(requestId != null ? requestId : "<none>").append('\n');
    content.append("BinaryContentId: ").append(binaryContentId.toString()).append('\n');
    content.append("Error: ").append(e.getMessage()).append('\n');

    // Spring Event로 발행하면 KafkaProduceRequiredEventListener가 Kafka로 전송합니다.
    try {
      eventPublisher.publishEvent(new com.sprint.mission.discodeit.event.S3UploadFailedEvent(binaryContentId, content.toString()));
    } catch (Exception ex) {
      log.error("Failed to publish S3UploadFailedEvent", ex);
    }

    throw e; // 다시 던져서 호출자(리스너 등)가 알 수 있게 함
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    String key = binaryContentId.toString();
    try {
      S3Client s3Client = getS3Client();

      GetObjectRequest request = GetObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .build();

      byte[] bytes = s3Client.getObjectAsBytes(request).asByteArray();
      return new ByteArrayInputStream(bytes);
    } catch (S3Exception e) {
      log.error("S3에서 파일 다운로드 실패: {}", e.getMessage());
      throw new NoSuchElementException("File with key " + key + " does not exist");
    }
  }

  private S3Client getS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            )
        )
        .build();
  }

  @Override
  public ResponseEntity<Void> download(BinaryContentDto metaData) {
    try {
      String key = metaData.id().toString();
      String presignedUrl = generatePresignedUrl(key, metaData.contentType());

      log.info("생성된 Presigned URL: {}", presignedUrl);

      return ResponseEntity
          .status(HttpStatus.FOUND)
          .header(HttpHeaders.LOCATION, presignedUrl)
          .build();
    } catch (Exception e) {
      log.error("Presigned URL 생성 실패: {}", e.getMessage());
      throw new RuntimeException("Presigned URL 생성 실패", e);
    }
  }

  private String generatePresignedUrl(String key, String contentType) {
    try (S3Presigner presigner = getS3Presigner()) {
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .responseContentType(contentType)
          .build();

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofSeconds(presignedUrlExpirationSeconds))
          .getObjectRequest(getObjectRequest)
          .build();

      PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
      return presignedRequest.url().toString();
    }
  }

  private S3Presigner getS3Presigner() {
    return S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            )
        )
        .build();
  }
} 