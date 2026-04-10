package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

/**
 * S3 기반 Binary Storage 구현체
 * 조건:
 * storage.type = s3 일 때만 Bean 등록
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3Client s3Client;
  private final S3Properties properties;

  /**
   * 파일 업로드
   */
  @Override
  public UUID put(UUID id, byte[] content) {

    String key = id.toString();

    s3Client.putObject(
        PutObjectRequest.builder()
            .bucket(properties.getBucket())
            .key(key)
            .contentType("application/octet-stream")
            .build(),
        RequestBody.fromBytes(content)
    );

    return id;
  }

  /**
   * 파일 다운로드 (InputStream 형태)
   */
  @Override
  public InputStream get(UUID id) {

    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(properties.getBucket())
        .key(id.toString())
        .build();

    return s3Client.getObject(request);
  }

  /**
   * Presigned URL 기반 다운로드 (리다이렉트 방식)
   */
  @Override
  public ResponseEntity<Void> download(BinaryContentDto dto) {

    String url = generatePresignedUrl(dto.id().toString());

    return ResponseEntity
        .status(302)
        .header(HttpHeaders.LOCATION, url)
        .build();
  }

  /**
   * Presigned URL 생성
   */
  private String generatePresignedUrl(String key) {

    try (S3Presigner presigner = S3Presigner.builder()
        .region(software.amazon.awssdk.regions.Region.of(properties.getRegion()))
        .credentialsProvider(
            software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create(
                software.amazon.awssdk.auth.credentials.AwsBasicCredentials.create(
                    properties.getAccessKey(),
                    properties.getSecretKey()
                )
            )
        )
        .build()) {

      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(properties.getBucket())
          .key(key)
          .build();

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofSeconds(
              properties.getPresignedUrlExpiration()
          ))
          .getObjectRequest(getObjectRequest)
          .build();

      return presigner.presignGetObject(presignRequest)
          .url()
          .toString();
    }
  }
}