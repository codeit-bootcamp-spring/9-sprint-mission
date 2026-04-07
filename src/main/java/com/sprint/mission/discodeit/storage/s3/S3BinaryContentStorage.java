package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
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
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucket;
  private final long presignedUrlExpiration;

  public S3BinaryContentStorage(
      @Value("${discodeit.storage.s3.access-key}") String accessKey,
      @Value("${discodeit.storage.s3.secret-key}") String secretKey,
      @Value("${discodeit.storage.s3.region}") String region,
      @Value("${discodeit.storage.s3.bucket}") String bucket,
      @Value("${discodeit.storage.s3.presigned-url-expiration}") long presignedUrlExpiration
  ) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
    this.bucket = bucket;
    this.presignedUrlExpiration = presignedUrlExpiration;
  }

  private S3Client getS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        ))
        .build();
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    try (S3Client s3 = getS3Client()) {
      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucket)
          .key(id.toString())
          .build();
      s3.putObject(request, RequestBody.fromBytes(bytes));
      return id;
    }
  }

  @Override
  public InputStream get(UUID id) {
    try (S3Client s3 = getS3Client()) {
      GetObjectRequest request = GetObjectRequest.builder()
          .bucket(bucket)
          .key(id.toString())
          .build();
      return s3.getObject(request);
    }
  }

  private String generatePresignedUrl(String key, String contentType) {
    try (S3Presigner presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        ))
        .build()) {

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
          .getObjectRequest(GetObjectRequest.builder()
              .bucket(bucket)
              .key(key)
              .build())
          .build();

      return presigner.presignGetObject(presignRequest).url().toString();
    }
  }

  @Override
  public ResponseEntity<Void> download(BinaryContentDto dto) {
    String presignedUrl = generatePresignedUrl(
        dto.id().toString(),
        dto.contentType()
    );

    return ResponseEntity
        .status(HttpStatus.FOUND)  // 302 리다이렉트
        .header(HttpHeaders.LOCATION, presignedUrl)
        .build();
  }
}
