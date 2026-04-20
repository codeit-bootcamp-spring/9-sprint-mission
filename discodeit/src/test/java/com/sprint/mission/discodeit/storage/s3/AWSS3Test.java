package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.time.Duration;

@SpringBootTest
class AWSS3Test {

  @Autowired
  private S3Client s3Client;

  @Autowired
  private S3Properties properties;

  // 업로드 테스트
  @Test
  void uploadTest() {
    String key = "test.txt";

    s3Client.putObject(
        PutObjectRequest.builder()
            .bucket(properties.getBucket())
            .key(key)
            .build(),
        RequestBody.fromString("hello s3")
    );
  }

  // 다운로드 테스트
  @Test
  void downloadTest() {
    String key = "test.txt";

    String content = s3Client.getObjectAsBytes(
        GetObjectRequest.builder()
            .bucket(properties.getBucket())
            .key(key)
            .build()
    ).asUtf8String();

    System.out.println(content);
  }

  // Presigned URL 생성 테스트
  @Test
  void presignedUrlTest() {
    S3Presigner presigner = S3Presigner.builder()
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

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(properties.getBucket())
        .key("test.txt")
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(getObjectRequest)
        .build();

    String url = presigner.presignGetObject(presignRequest).url().toString();

    System.out.println(url);
  }
}