package com.sprint.mission.discodeit.storage.s3;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;
import java.io.FileInputStream;

public class AWSS3Test {

  private String bucketName;
  private S3Client s3Client;
  private S3Presigner presigner;

  public AWSS3Test() throws Exception {
    Properties prop = new Properties();
    prop.load(new FileInputStream(".env"));

    String accessKey = prop.getProperty("AWS_ACCESS_KEY_ID");
    String secretKey = prop.getProperty("AWS_SECRET_ACCESS_KEY");
    String regionStr = prop.getProperty("AWS_REGION");
    this.bucketName = prop.getProperty("AWS_S3_BUCKET_NAME");

    Region region = Region.of(regionStr);
    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

    this.s3Client = S3Client.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    this.presigner = S3Presigner.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();
  }

  public void testUpload(String key, String filePath) {
    PutObjectRequest putOb = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    s3Client.putObject(putOb, Paths.get(filePath));
    System.out.println("업로드 성공: " + key);
  }


  public void testDownload(String key, String downloadPath) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    s3Client.getObject(getObjectRequest, Paths.get(downloadPath));
    System.out.println("다운로드 성공: " + downloadPath);
  }

  public void testGetPresignedUrl(String key) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10)) // 10분간 유효
        .getObjectRequest(getObjectRequest)
        .build();

    PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
    System.out.println("🔗 생성된 Presigned URL: " + presignedRequest.url().toString());
  }

  public static void main(String[] args) {
    try {
      AWSS3Test tester = new AWSS3Test();

      tester.testUpload("test-image.jpg", "discodeit/uploads/test.jpeg");
      tester.testDownload("test-image.jpg", "discodeit/download/result.jpg");
      tester.testGetPresignedUrl("test-image.jpg");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}