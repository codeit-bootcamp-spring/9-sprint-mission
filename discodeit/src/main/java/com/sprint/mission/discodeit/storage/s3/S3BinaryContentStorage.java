package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.core.io.Resource;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

// discodeit.storage.type 값이 s3일 때만 이 클래스를 작동시키는 스프링 설정
@Component
@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucket;
  private final NotificationService notificationService;
  private final UserRepository userRepository;

  // application.yaml에 적어둔 설정값을 자바 변수로 끌어오는 생성자
  public S3BinaryContentStorage(
      @Value("${discodeit.storage.s3.access-key}") String accessKey,
      @Value("${discodeit.storage.s3.secret-key}") String secretKey,
      @Value("${discodeit.storage.s3.region}") String region,
      @Value("${discodeit.storage.s3.bucket}") String bucket,
      NotificationService notificationService,
      UserRepository userRepository) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
    this.bucket = bucket;
    this.notificationService = notificationService;
    this.userRepository = userRepository;
  }

  @Retryable(
      retryFor = Exception.class,
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000)
  )
  @Override
  public UUID put(UUID id, byte[] data) {
    S3Client s3Client = getS3Client();
    String key = id.toString();

    PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    s3Client.putObject(request, RequestBody.fromBytes(data));
    return id;
  }

  @Override
  public InputStream get(UUID id) {
    S3Client s3Client = getS3Client();
    String key = id.toString();

    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    // S3에서 파일을 데이터 흐름(Stream) 형태로 가져옴
    return s3Client.getObject(request);
  }

  @Recover
  public UUID recover(Exception e, UUID id, byte[] data) {
    String requestId = MDC.get("requestId");

    String message = String.format(
        "RequestId: %s\nBinaryContentId: %s\nError: %s",
        requestId, id, e.getMessage()
    );

    log.error("S3 파일 업로드 최종 실패 - {}", message);

    // ADMIN 유저들에게 알림 전송
    userRepository.findAll().stream()
        .filter(user -> user.getRole() == Role.ADMIN)
        .forEach(admin ->
            notificationService.create(admin, "S3 파일 업로드 실패", message)
        );

    throw new RuntimeException("S3 업로드 최종 실패: " + e.getMessage());
  }

  @Override
  public ResponseEntity<? extends Resource> download(BinaryContentDto dto) {
    String key = dto.id().toString();
    String url = generatePresignedUrl(key, dto.contentType());

    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(url))
        .<Resource>build();
  }

  private S3Client getS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }

  private String generatePresignedUrl(String key, String contentType) {
    S3Presigner presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
        .build();

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .responseContentType(contentType)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10)) // 10분 유효기간
        .getObjectRequest(getObjectRequest)
        .build();

    return presigner.presignGetObject(presignRequest).url().toString();
  }
}