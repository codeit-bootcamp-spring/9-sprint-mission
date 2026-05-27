package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import java.time.Duration;

@ActiveProfiles("test")
@SpringBootTest(properties = "discodeit.storage.type=s3")
public class AWSS3Test {

  @Value("${discodeit.storage.s3.access-key}")
  private String accessKey;

  @Value("${discodeit.storage.s3.secret-key}")
  private String secretKey;

  @Value("${discodeit.storage.s3.region}")
  private String region;

  @Value("${discodeit.storage.s3.bucket}")
  private String bucketName;

  private S3Client getS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }

  @Test
  public void s3UploadTest() {
    String key = "test/hello.txt";
    String content = "Hello S3! This is Hyun-ha's test file.";

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .contentType("text/plain")
        .build();

    getS3Client().putObject(putObjectRequest, RequestBody.fromString(content));
    System.out.println("업로드 성공: " + key);
  }

  @Test
  public void s3DownloadTest() {
    String key = "test/hello.txt";

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    ResponseBytes<GetObjectResponse> objectBytes = getS3Client().getObjectAsBytes(getObjectRequest);
    String data = objectBytes.asUtf8String();
    System.out.println("다운로드 내용: " + data);
  }

  @Test
  public void s3PresignedUrlTest() {
    String key = "test/hello.txt";

    S3Presigner presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build())
        .build();

    PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
    System.out.println("임시 접속 URL (10분 유효): " + presignedRequest.url());
  }
}