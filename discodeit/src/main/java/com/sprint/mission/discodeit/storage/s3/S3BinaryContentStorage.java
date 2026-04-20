package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3Client s3Client;
  private final S3Properties properties;

  private S3Presigner presigner;

  @PostConstruct
  public void init() {
    this.presigner = S3Presigner.builder()
        .region(software.amazon.awssdk.regions.Region.of(properties.getRegion()))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    properties.getAccessKey(),
                    properties.getSecretKey()
                )
            )
        )
        .build();
  }

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    String key = binaryContentId.toString();

    s3Client.putObject(
        PutObjectRequest.builder()
            .bucket(properties.getBucket())
            .key(key)
            .build(),
        software.amazon.awssdk.core.sync.RequestBody.fromBytes(bytes)
    );

    return binaryContentId;
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    String key = binaryContentId.toString();

    return s3Client.getObject(
        GetObjectRequest.builder()
            .bucket(properties.getBucket())
            .key(key)
            .build()
    );
  }

  @Override
  public ResponseEntity<Void> download(BinaryContentDto metaData) {
    String key = metaData.id().toString();

    String presignedUrl = generatePresignedUrl(key);

    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(presignedUrl))
        .build();
  }

  private String generatePresignedUrl(String key) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(properties.getBucket())
        .key(key)
        .build();

    GetObjectPresignRequest presignRequest =
        GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofSeconds(properties.getPresignedUrlExpiration()))
            .getObjectRequest(getObjectRequest)
            .build();

    return presigner.presignGetObject(presignRequest)
        .url()
        .toString();
  }
}