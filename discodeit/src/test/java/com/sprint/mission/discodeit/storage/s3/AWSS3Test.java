package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

@Disabled
public class AWSS3Test {

  private String accessKey;
  private String secretKey;
  private String region;
  private String bucketName;
  private S3Client s3Client;

  @BeforeEach
  public void setup() throws IOException {
    // 1. .env 파일 로드
    Properties props = new Properties();
    props.load(new FileInputStream(".env"));

    this.accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
    this.secretKey = props.getProperty("AWS_S3_SECRET_KEY");
    this.region = props.getProperty("AWS_S3_REGION");
    this.bucketName = props.getProperty("AWS_S3_BUCKET");

    // 2. S3Client 생성 (AWS S3와 통신)
    this.s3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }

  @Test
  public void s3UploadTest() {
    // [업로드 테스트] "hello.txt"라는 이름으로 "Hello S3!"라는 내용을 저장합니다.
    String key = "test/hello.txt";
    String content = "Hello S3! This is Hyun-ha's test file.";

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .contentType("text/plain")
        .build();

    s3Client.putObject(putObjectRequest, RequestBody.fromString(content));
    System.out.println("업로드 성공: " + key);
  }

  @Test
  public void s3DownloadTest() {
    // [다운로드 테스트] 방금 올린 파일을 다시 읽어와서 출력합니다.
    String key = "test/hello.txt";

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
    String data = objectBytes.asUtf8String();
    System.out.println("다운로드 내용: " + data);
  }

  @Test
  public void s3PresignedUrlTest() {
    // [Presigned URL 생성] 외부 사용자가 딱 10분 동안만 파일을 볼 수 있는 임시 주소를 만듭니다.
    String key = "test/hello.txt";

    S3Presigner presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10)) // 10분 유효
        .getObjectRequest(GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build())
        .build();

    PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
    System.out.println("임시 접속 URL (10분 유효): " + presignedRequest.url());
  }
}