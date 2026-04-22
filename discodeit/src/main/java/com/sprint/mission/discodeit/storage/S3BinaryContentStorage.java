package com.sprint.mission.discodeit.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3Client s3Client;
  private final S3Presigner s3Presigner;
  private final String bucketName;
  private final long expiration;

  public S3BinaryContentStorage(
      S3Client s3Client,
      S3Presigner s3Presigner,
      @Value("${discodeit.storage.s3.bucket}") String bucketName,
      @Value("${discodeit.storage.s3.presigned-url-expiration}") long expiration) {
    this.s3Client = s3Client;
    this.s3Presigner = s3Presigner;
    this.bucketName = bucketName;
    this.expiration = expiration;
  }

  @Override
  public void put(UUID id, byte[] bytes) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(id.toString())
        .build();
    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
  }

  @Override
  public Resource loadAsResource(UUID id) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(id.toString())
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofSeconds(expiration))
        .getObjectRequest(getObjectRequest)
        .build();

    String presignedUrl = s3Presigner.presignGetObject(presignRequest).url().toString();
    try {
      return new UrlResource(presignedUrl);
    } catch (MalformedURLException e) {
      throw new RuntimeException("Presigned URL 생성 중 오류 발생", e);
    }
  }

  @Override
  public void delete(UUID id) {
    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
        .bucket(bucketName)
        .key(id.toString())
        .build();
    s3Client.deleteObject(deleteObjectRequest);
  }
}