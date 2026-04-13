package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSS3Test {

  private S3Client s3Client;
  private S3Presigner s3Presigner;
  private String bucketName;
  private final String testObjectKey = "test-folder/hello-s3.txt"; // S3에 저장될 파일명(경로)
  private final String testContent = "Hello S3 Upload Test!";

  @BeforeAll
  void setUp() throws IOException {
    // 1. Properties 클래스를 활용하여 .env 파일 로드
    Properties props = new Properties();
    try (FileInputStream fis = new FileInputStream(".env")) {
      props.load(fis);
    } catch (IOException e) {
      System.err.println(".env 파일을 찾을 수 없습니다. 프로젝트 루트 경로에 있는지 확인해주세요.");
      throw e;
    }

    // 2. .env에서 AWS 정보 가져오기 (이전에 작성한 키 이름과 일치해야 함)
    String accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
    String secretKey = props.getProperty("AWS_S3_SECRET_KEY");
    String regionString = props.getProperty("AWS_S3_REGION");
    this.bucketName = props.getProperty("AWS_S3_BUCKET");

    // 3. 자격 증명 및 리전 설정
    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
    Region region = Region.of(regionString);

    // 4. S3 통신을 위한 Client 및 URL 생성을 위한 Presigner 초기화
    s3Client = S3Client.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    s3Presigner = S3Presigner.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();
  }

  @Test
  @Order(1)
  @DisplayName("1. S3 파일 업로드 테스트")
  void testUpload() {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(testObjectKey)
        .build();

    // 텍스트 데이터를 RequestBody로 변환하여 업로드
    s3Client.putObject(putObjectRequest, RequestBody.fromString(testContent));
    System.out.println("✅ 업로드 성공! Object Key: " + testObjectKey);
  }

  @Test
  @Order(2)
  @DisplayName("2. S3 파일 다운로드 테스트")
  void testDownload() {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(testObjectKey)
        .build();

    // 💡 수정된 부분: ResponseTransformer.toBytes()를 사용하여 바이트로 읽어옵니다.
    ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes());
    String downloadedContent = new String(objectBytes.asByteArray(), StandardCharsets.UTF_8);

    System.out.println("✅ 다운로드 성공! 읽어온 내용: " + downloadedContent);

    // 업로드했던 내용과 다운로드한 내용이 같은지 검증
    assertEquals(testContent, downloadedContent);
  }

  @Test
  @Order(3)
  @DisplayName("3. S3 Presigned URL 생성 테스트")
  void testPresignedUrl() {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(testObjectKey)
        .build();

    // 10분 동안만 유효한 임시 URL 생성 요청 만들기
    GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(getObjectRequest)
        .build();

    PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
    String presignedUrl = presignedGetObjectRequest.url().toString();

    System.out.println("✅ Presigned URL 생성 성공!");
    System.out.println("👉 URL: " + presignedUrl);

    assertNotNull(presignedUrl);
  }

  @AfterAll
  void tearDown() {
    // 테스트 종료 후 자원 반납
    if (s3Client != null) s3Client.close();
    if (s3Presigner != null) s3Presigner.close();
  }
}