package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@Tag("aws")
public class AWSS3Test {

  @Autowired
  private S3Client s3Client;

  @Autowired
  private S3Properties properties;

  // 1. 업로드 테스트
  @Test
  void uploadTest() {
    String key = "test/" + UUID.randomUUID();
    String content = "hello s3";

    s3Client.putObject(
        PutObjectRequest.builder()
            .bucket(properties.getBucket())
            .key(key)
            .contentType("text/plain")
            .build(),
        RequestBody.fromString(content)
    );

    System.out.println("UPLOAD SUCCESS: " + key);
  }

  // 2. 다운로드 테스트
  @Test
  void downloadTest() {
    String key = "test/" + UUID.randomUUID();
    String expected = "hello s3";
    putTextObject(key, expected);

    String result = s3Client.getObjectAsBytes(
        GetObjectRequest.builder()
            .bucket(properties.getBucket())
            .key(key)
            .build()
    ).asUtf8String();

    assertEquals(expected, result);

    System.out.println("DOWNLOAD RESULT: " + result);
  }

  // 3. Presigned URL 테스트
  @Test
  void presignedUrlTest() {
    String key = "test/" + UUID.randomUUID();
    putTextObject(key, "hello s3");

    try (S3Presigner presigner = S3Presigner.builder()
        .region(Region.of(properties.getRegion()))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
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
          .signatureDuration(Duration.ofMinutes(10))
          .getObjectRequest(getObjectRequest)
          .build();

      PresignedGetObjectRequest presignedRequest =
          presigner.presignGetObject(presignRequest);

      URL url = presignedRequest.url();

      System.out.println("PRESIGNED URL: " + url);
    }
  }

  private void putTextObject(String key, String content) {
    s3Client.putObject(
        PutObjectRequest.builder()
            .bucket(properties.getBucket())
            .key(key)
            .contentType("text/plain")
            .build(),
        RequestBody.fromString(content)
    );
  }
}