package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AWSS3Test {

  private S3Client s3Client;
  private S3Presigner s3Presigner;
  private String bucketName;

  @BeforeEach
  void setUp() {

    String accessKey = System.getenv("AWS_S3_ACCESS_KEY");
    String secretKey = System.getenv("AWS_S3_SECRET_KEY");
    String region = System.getenv("AWS_S3_REGION");
    this.bucketName = System.getenv("AWS_S3_BUCKET");

    if (accessKey == null || secretKey == null || region == null || bucketName == null) {
      throw new IllegalStateException("필수 AWS 환경 변수가 설정되지 않았습니다.");
    }

    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

    this.s3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    this.s3Presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();
  }

  @Test
  void testUpload() {
    String key = "test-" + UUID.randomUUID();
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    s3Client.putObject(putObjectRequest, RequestBody.fromString("Hello S3"));
  }

  @Test
  void testPresignedUrl() {
    String key = "test-file";
    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(builder -> builder.bucket(bucketName).key(key))
        .build();

    String url = s3Presigner.presignGetObject(presignRequest).url().toString();
    System.out.println("Generated URL: " + url);
    assertThat(url).contains(bucketName);
  }
}