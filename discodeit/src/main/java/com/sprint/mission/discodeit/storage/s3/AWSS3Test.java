package com.sprint.mission.discodeit.storage.s3;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class AWSS3Test {

  // .env 파일에 정의한 환경 변수들을 읽어옵니다.
  private final String accessKey = System.getenv("AWS_S3_ACCESS_KEY");
  private final String secretKey = System.getenv("AWS_S3_SECRET_KEY");
  private final String region = System.getenv("AWS_S3_REGION");
  private final String bucketName = System.getenv("AWS_S3_BUCKET");

  private final S3Client s3Client;
  private final S3Presigner s3Presigner;

  public AWSS3Test() {
    // 1. S3와 실제 통신을 담당하는 클라이언트 생성
    this.s3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();

    // 2. 일정 시간만 유효한 보안 링크(Presigned URL)를 만들어주는 생성기
    this.s3Presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }

  // [테스트 1] 업로드: S3 버킷에 "test/test.txt" 경로로 글자를 저장합니다.
  public void testUpload() {
    System.out.println(">>> S3 업로드 테스트를 시작합니다.");
    String content = "Hello S3! This is a test from Discodeit.";
    String key = "test/test.txt";

    s3Client.putObject(PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build(),
        RequestBody.fromString(content));

    System.out.println("성공: S3에 '" + key + "' 파일이 저장되었습니다.");
  }

  // [테스트 2] 다운로드: S3에 저장된 파일을 다시 읽어와서 내용을 확인합니다.
  public void testDownload() {
    System.out.println(">>> S3 다운로드 테스트를 시작합니다.");
    String key = "test/test.txt";

    ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(
        GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build());

    String data = objectBytes.asString(StandardCharsets.UTF_8);
    System.out.println("성공: S3에서 가져온 내용 -> " + data);
  }

  // [테스트 3] Presigned URL 생성: 누구나 클릭해서 10분 동안만 볼 수 있는 비밀 링크를 만듭니다.
  public void testPresignedUrl() {
    System.out.println(">>> Presigned URL 생성 테스트를 시작합니다.");
    String key = "test/test.txt";

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10)) // 10분 동안 유효
        .getObjectRequest(GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build())
        .build();

    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
    System.out.println("성공: 아래 링크를 브라우저에 붙여넣어 보세요 (10분 유효)");
    System.out.println(presignedRequest.url());
  }

  public static void main(String[] args) {
    AWSS3Test test = new AWSS3Test();
    try {
      test.testUpload();
      test.testDownload();
      test.testPresignedUrl();
      System.out.println("🌟 모든 AWS S3 연동 테스트가 성공적으로 끝났습니다!");
    } catch (Exception e) {
      System.err.println("❌ 테스트 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }
}