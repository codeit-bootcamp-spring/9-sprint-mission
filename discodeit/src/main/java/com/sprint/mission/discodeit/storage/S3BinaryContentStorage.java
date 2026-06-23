package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
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
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class  S3BinaryContentStorage implements BinaryContentStorage{

  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucket;
  private final S3Client s3Client;
  private final S3Presigner s3Presigner;
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final ApplicationEventPublisher eventPublisher;

  public S3BinaryContentStorage(
      @Value("${discodeit.storage.s3.access-key}") String accessKey,
      @Value("${discodeit.storage.s3.secret-key}") String secretKey,
      @Value("${discodeit.storage.s3.region}") String region,
      @Value("${discodeit.storage.s3.bucket}") String bucket,
      NotificationRepository notificationRepository, UserRepository userRepository, ApplicationEventPublisher eventPublisher) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
    this.bucket = bucket;
    this.notificationRepository = notificationRepository;
    this.userRepository = userRepository;
    this.eventPublisher = eventPublisher;

    AwsBasicCredentials credentials = AwsBasicCredentials.create(this.accessKey, this.secretKey);
    Region awsRegion = Region.of(this.region);

    this.s3Client = S3Client.builder()
        .region(awsRegion)
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    this.s3Presigner = S3Presigner.builder()
        .region(awsRegion)
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();
  }
  @Retryable(
      retryFor = {Exception.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000, multiplier = 2)
  )

  @Override
  public UUID put(UUID id, byte[] data) {
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread interrupted while simulating delay", e);
    }

    PutObjectRequest request = PutObjectRequest.builder()
        .bucket(this.bucket)
        .key(id.toString())
        .build();

    getS3Client().putObject(request, RequestBody.fromBytes(data));
    return id;
  }

  @Recover
  public UUID recover(Exception e, UUID id, byte[] data) {
    String requestId = MDC.get("requestId");
    log.error("바이너리 데이터 저장 최종 실패 - RequestId: {}, BinaryContentId: {}, Error: {}",
        requestId, id, e.getMessage());
    eventPublisher.publishEvent(
        new S3UploadFailedEvent(id, e.getMessage(), requestId)
    );
    return id;
  }

  @Override
  public InputStream get(UUID id) {
    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(this.bucket)
        .key(id.toString())
        .build();

    return getS3Client().getObject(request);
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto dto) {
    // 💡 record의 접근자 방식인 dto.id()와 dto.contentType()을 사용합니다.
    String presignedUrl = generatePresignedUrl(dto.id().toString(), dto.contentType());

    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(presignedUrl))
        .build();
  }
  private S3Client getS3Client() {
    return this.s3Client;
  }

  private String generatePresignedUrl(String key, String contentType) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(this.bucket)
        .key(key)
        .responseContentType(contentType)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10)) // 10분 유효
        .getObjectRequest(getObjectRequest)
        .build();

    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
    return presignedRequest.url().toString();
  }
}
