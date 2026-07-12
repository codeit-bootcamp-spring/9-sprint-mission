package com.sprint.mission.discodeit.storage.s3;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Configuration
public class AWSS3Test {

  private String accessKey;
  private String secretKey;
  private String region;
  private String bucketName;

  public void loadEnv() throws Exception {
    Properties props = new Properties();
    try (FileInputStream fis = new FileInputStream(".env")) {
      props.load(fis);
    }
    this.accessKey = props.getProperty("AWS_S3_ACCESS_KEY").trim();
    this.secretKey = props.getProperty("AWS_S3_SECRET_KEY").trim();
    this.region = props.getProperty("AWS_S3_REGION").trim();
    this.bucketName = props.getProperty("AWS_S3_BUCKET").trim();
  }

  private S3Client getS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        )).build();
  }

  public void putObject(String key, String path) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(key)
        .build();
    getS3Client().putObject(putObjectRequest, Paths.get(path));
  }

  public void getObject(String key, String downloadPath) throws Exception {
    GetObjectRequest gor = GetObjectRequest.builder()
        .bucket(bucketName).key(key).build();

    try (InputStream stream = getS3Client().getObject(gor);
        FileOutputStream fos = new FileOutputStream(downloadPath)) {
      stream.transferTo(fos);
    }

  }

  public String generatePresignedUrl(String key) {
    try (S3Presigner s3Presigner = S3Presigner.builder().region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build()) {
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(bucketName).key(key).build();

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(10))
          .getObjectRequest(getObjectRequest)
          .build();

      PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
      return presignedRequest.url().toString();
    }
  }


}
