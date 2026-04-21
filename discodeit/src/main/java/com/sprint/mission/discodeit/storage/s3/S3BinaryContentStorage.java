package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
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
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@Component
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3Client s3Client;
  private final S3Presigner s3Presigner;
  private final String bucket;
  private final long presignedUrlExpiration;

  @Autowired
  public S3BinaryContentStorage(
      @Value("${discodeit.storage.s3.access-key}") String accessKey,
      @Value("${discodeit.storage.s3.secret-key}") String secretKey,
      @Value("${discodeit.storage.s3.region}") String region,
      @Value("${discodeit.storage.s3.bucket}") String bucket,
      @Value("${discodeit.storage.s3.presigned-url-expiration:600}") long presignedUrlExpiration
  ) {
    if (accessKey == null || accessKey.isBlank()) {
      throw new IllegalArgumentException(
          "S3 access-key가 설정되지 않았습니다. 환경 변수 AWS_S3_ACCESS_KEY를 확인하세요.");
    }
    if (secretKey == null || secretKey.isBlank()) {
      throw new IllegalArgumentException(
          "S3 secret-key가 설정되지 않았습니다. 환경 변수 AWS_S3_SECRET_KEY를 확인하세요.");
    }
    if (region == null || region.isBlank()) {
      throw new IllegalArgumentException(
          "S3 region이 설정되지 않았습니다. 환경 변수 AWS_S3_REGION을 확인하세요.");
    }
    if (bucket == null || bucket.isBlank()) {
      throw new IllegalArgumentException(
          "S3 bucket이 설정되지 않았습니다. 환경 변수 AWS_S3_BUCKET을 확인하세요.");
    }

    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
    StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
    Region awsRegion = Region.of(region);

    this.s3Client = S3Client.builder()
        .region(awsRegion)
        .credentialsProvider(credentialsProvider)
        .build();
    this.s3Presigner = S3Presigner.builder()
        .region(awsRegion)
        .credentialsProvider(credentialsProvider)
        .build();
    this.bucket = bucket;
    this.presignedUrlExpiration = presignedUrlExpiration;
  }

  S3BinaryContentStorage(S3Client s3Client, S3Presigner s3Presigner, String bucket,
      long presignedUrlExpiration) {
    this.s3Client = s3Client;
    this.s3Presigner = s3Presigner;
    this.bucket = bucket;
    this.presignedUrlExpiration = presignedUrlExpiration;
  }

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucket)
        .key(binaryContentId.toString())
        .build();
    s3Client.putObject(request, RequestBody.fromBytes(bytes));
    return binaryContentId;
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(bucket)
        .key(binaryContentId.toString())
        .build();
    try {
      return s3Client.getObject(request);
    } catch (NoSuchKeyException e) {
      throw new DiscodeitException(ErrorCode.BINARY_CONTENT_NOT_FOUND,
          Map.of("binaryContentId", binaryContentId));
    }
  }

  @Override
  public ResponseEntity<?> download(BinaryContentResponse metaData) {
    String presignedUrl = generatePresignedUrl(metaData.id());
    return ResponseEntity
        .status(HttpStatus.FOUND)
        .header(HttpHeaders.LOCATION, presignedUrl)
        .build();
  }

  private String generatePresignedUrl(UUID binaryContentId) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(binaryContentId.toString())
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
        .getObjectRequest(getObjectRequest)
        .build();

    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
    return presignedRequest.url().toString();
  }
}
