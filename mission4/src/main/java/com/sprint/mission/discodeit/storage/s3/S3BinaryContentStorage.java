package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

public class S3BinaryContentStorage implements BinaryContentStorage {

  private String accessKey;
  private String secretKey;
  private String region;
  private String bucket;

  public S3BinaryContentStorage(String accessKey, String secretKey, String region, String bucket) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
    this.bucket = bucket;
  }

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucket).key(binaryContentId.toString()).build();
    getS3client().putObject(request, RequestBody.fromBytes(bytes));
    return binaryContentId;
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(bucket).key(binaryContentId.toString()).build();
    return getS3client().getObject(request);

  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto metaData) {
    String presignedUrl = generatePresignedUrl(
        metaData.id().toString(),
        metaData.contentType()
    );
    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(presignedUrl))
        .build();
  }

  public S3Client getS3client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        )).build();
  }

  public String generatePresignedUrl(String key, String contentType) {
    try (S3Presigner presigner = S3Presigner.builder().region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
        .build()) {
      GetObjectRequest request = GetObjectRequest.builder()
          .bucket(bucket).key(key).responseContentType(contentType).build();

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(10))
          .getObjectRequest(request).build();
      return presigner.presignGetObject(presignRequest).url().toString();
    }
  }
}
