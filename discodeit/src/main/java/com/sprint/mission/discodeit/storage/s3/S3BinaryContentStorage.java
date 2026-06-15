package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.MDCLoggingInterceptor;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
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

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucket;
  private final long expiration;
  private final ApplicationEventPublisher eventPublisher;

  public S3BinaryContentStorage(
      @Value("${discodeit.storage.s3.access-key}") String accessKey,
      @Value("${discodeit.storage.s3.secret-key}") String secretKey,
      @Value("${discodeit.storage.s3.region}") String region,
      @Value("${discodeit.storage.s3.bucket}") String bucket,
      @Value("${discodeit.storage.s3.presigned-url-expiration}") long expiration,
      ApplicationEventPublisher eventPublisher
  ) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
    this.bucket = bucket;
    this.expiration = expiration;
    this.eventPublisher = eventPublisher;
  }

  private S3Client getS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }

  private String generatePresignedURI(String key, String contentType) {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

    S3Presigner s3Presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofSeconds(expiration))
        .getObjectRequest(GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .responseContentType(contentType)
            .build())
        .build();

    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
    String url = presignedRequest.url().toString();

    s3Presigner.close();
    return url;
  }

  @Override
  @Retryable(
      retryFor = RuntimeException.class,
      maxAttemptsExpression = "${discodeit.storage.s3.retry.max-attempts:3}",
      backoff = @Backoff(
          delayExpression = "${discodeit.storage.s3.retry.delay-ms:1000}",
          multiplierExpression = "${discodeit.storage.s3.retry.multiplier:2}"
      )
  )
  public UUID put(UUID id, byte[] data) {
    getS3Client().putObject(PutObjectRequest.builder()
        .bucket(bucket)
        .key(id.toString())
        .build(), RequestBody.fromBytes(data));
    return id;
  }

  @Recover
  public UUID recover(RuntimeException exception, UUID id, byte[] data) {
    notifyAdmins(id, exception);
    throw exception;
  }

  @Override
  public InputStream get(UUID id) {
    return getS3Client().getObject(GetObjectRequest.builder()
        .bucket(bucket)
        .key(id.toString())
        .build());
  }

  @Override
  public ResponseEntity<Void> download(BinaryContentDto dto) {
    String s3Key = dto.id().toString();
    String contentType = dto.contentType();
    String presignedUrl = generatePresignedURI(s3Key, contentType);

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create(presignedUrl));

    return new ResponseEntity<>(headers, HttpStatus.FOUND);
  }

  private void notifyAdmins(UUID binaryContentId, RuntimeException exception) {
    String requestId = MDC.get(MDCLoggingInterceptor.REQUEST_ID);
    eventPublisher.publishEvent(new S3UploadFailedEvent(
        binaryContentId,
        requestId == null ? "N/A" : requestId,
        "S3 binary content upload",
        exception.getMessage()
    ));
  }
}
