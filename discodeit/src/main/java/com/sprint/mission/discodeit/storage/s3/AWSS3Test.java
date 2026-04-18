package com.sprint.mission.discodeit.storage.s3;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AWSS3Test {

  private final S3Properties s3Properties;
  private S3Client s3Client;
  private S3Presigner s3Presigner;

  @PostConstruct
  public void init() {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(
        s3Properties.getAccessKey(),
        s3Properties.getSecretKey()
    );

    this.s3Client = S3Client.builder()
        .region(Region.of(s3Properties.getRegion()))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    this.s3Presigner = S3Presigner.builder()
        .region(Region.of(s3Properties.getRegion()))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();
  }

  public void upload(String key, byte[] data, String contentType) {
    PutObjectRequest putRequest = PutObjectRequest.builder()
        .bucket(s3Properties.getBucket())
        .key(key)
        .contentType(contentType)
        .build();

    s3Client.putObject(putRequest, RequestBody.fromBytes(data));
  }

  public byte[] download(String key) {
    GetObjectRequest getRequest = GetObjectRequest.builder()
        .bucket(s3Properties.getBucket())
        .key(key)
        .build();

    return s3Client.getObject(getRequest, ResponseTransformer.toBytes()).asByteArray();
  }

  public String generatePresignedUrl(String key, int expirationMinutes) {
    GetObjectRequest getRequest = GetObjectRequest.builder()
        .bucket(s3Properties.getBucket())
        .key(key)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(expirationMinutes))
        .getObjectRequest(getRequest)
        .build();

    return s3Presigner.presignGetObject(presignRequest).url().toString();
  }
}