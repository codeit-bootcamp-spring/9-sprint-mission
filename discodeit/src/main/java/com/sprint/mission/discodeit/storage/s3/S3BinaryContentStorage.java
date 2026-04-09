package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

// 다이어그램 완벽 반영 + 적용
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  // 멤버 변수와 동일하게 정의
  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucket;
  private final long expiration;

  // 다이어그램과 동일한 생성자 정의
  public S3BinaryContentStorage(
      @Value("${discodeit.storage.s3.access-key}") String accessKey,
      @Value("${discodeit.storage.s3.secret-key}") String secretKey,
      @Value("${discodeit.storage.s3.region}") String region,
      @Value("${discodeit.storage.s3.bucket}") String bucket,
      @Value("${discodeit.storage.s3.presigned-url-expiration}") long expiration) {

    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
    this.bucket = bucket;
    this.expiration = expiration;
  }

  // 프라이빗 헬퍼 메소드 구현 (S3Client 생성)
  private S3Client getS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }

  // 프라이빗 헬퍼 메소드 구현 (Presigned URI 생성)
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

    s3Presigner.close(); // 사용 후 닫아줍니다.
    return url;
  }

  // `put` 메소드 구현
  @Override
  public UUID put(UUID id, byte[] data) {
    getS3Client().putObject(PutObjectRequest.builder()
        .bucket(bucket)
        .key(id.toString())
        .build(), RequestBody.fromBytes(data));
    return id;
  }

  // `get` 메소드 구현
  @Override
  public InputStream get(UUID id) {
    return getS3Client().getObject(GetObjectRequest.builder()
        .bucket(bucket)
        .key(id.toString())
        .build());
  }

  // `download` 메소드 구현 + 리다이렉트 적용
  @Override
  public ResponseEntity<Void> download(BinaryContentDto dto) {
    String s3Key = dto.id().toString();
    String contentType = dto.contentType();

    // Presigned URI 생성 헬퍼 호출
    String presignedUrl = generatePresignedURI(s3Key, contentType);

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create(presignedUrl));

    // 302 Found 상태코드로 리다이렉트 응답
    return new ResponseEntity<>(headers, HttpStatus.FOUND);
  }
}