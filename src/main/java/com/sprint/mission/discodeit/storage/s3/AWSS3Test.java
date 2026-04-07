package com.sprint.mission.discodeit.storage.s3;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

public class AWSS3Test {

  private static final Properties properties = new Properties();
  private static String accessKey;
  private static String secretKey;
  private static String region;
  private static String bucket;

  // .env 파일에서 AWS 정보 로드
  static {
    try (FileInputStream fis = new FileInputStream(".env")) {
      properties.load(fis);
      accessKey = properties.getProperty("AWS_ACCESS_KEY_ID");
      secretKey = properties.getProperty("AWS_SECRET_ACCESS_KEY");
      region = properties.getProperty("AWS_REGION");
      bucket = properties.getProperty("AWS_S3_BUCKET");
    } catch (IOException e) {
      throw new RuntimeException(".env 파일을 읽을 수 없습니다", e);
    }
  }

  // S3 클라이언트 생성
  private static S3Client createS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        ))
        .build();
  }

  // S3 Presigner 생성
  private static S3Presigner createPresigner() {
    return S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        ))
        .build();
  }

  // 업로드 테스트
  public static void testUpload() {
    System.out.println("=== 업로드 테스트 시작 ===");
    try (S3Client s3 = createS3Client()) {
      String key = "test/test-file.txt";
      byte[] content = "Hello, S3!".getBytes();

      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .contentType("text/plain")
          .build();

      s3.putObject(request, RequestBody.fromBytes(content));
      System.out.println("업로드 성공! key: " + key);
    } catch (Exception e) {
      System.out.println("업로드 실패: " + e.getMessage());
    }
  }

  // 다운로드 테스트
  public static void testDownload() {
    System.out.println("=== 다운로드 테스트 시작 ===");
    try (S3Client s3 = createS3Client()) {
      String key = "test/test-file.txt";

      GetObjectRequest request = GetObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .build();

      ResponseInputStream<GetObjectResponse> response = s3.getObject(request);
      String content = new String(response.readAllBytes());
      System.out.println("다운로드 성공! 내용: " + content);
    } catch (Exception e) {
      System.out.println("다운로드 실패: " + e.getMessage());
    }
  }

  // PresignedUrl 생성 테스트
  public static void testPresignedUrl() {
    System.out.println("=== PresignedUrl 생성 테스트 시작 ===");
    try (S3Presigner presigner = createPresigner()) {
      String key = "test/test-file.txt";

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(10))  // 10분간 유효
          .getObjectRequest(GetObjectRequest.builder()
              .bucket(bucket)
              .key(key)
              .build())
          .build();

      PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
      URL url = presignedRequest.url();
      System.out.println("PresignedUrl 생성 성공!");
      System.out.println("URL: " + url);
    } catch (Exception e) {
      System.out.println("PresignedUrl 생성 실패: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    testUpload();
    testDownload();
    testPresignedUrl();
  }
}