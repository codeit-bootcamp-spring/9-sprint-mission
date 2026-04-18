package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@RequiredArgsConstructor
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3Client s3Client;
  private final S3Presigner s3Presigner;
  private final S3Properties s3Properties;

  @Override
  public UUID put(UUID id, byte[] data) {
    String key = id.toString();
    PutObjectRequest putOb = PutObjectRequest.builder()
        .bucket(s3Properties.getBucket())
        .key(key)
        .build();

    s3Client.putObject(putOb, RequestBody.fromBytes(data));
    return id;
  }

  @Override
  public InputStream get(UUID id) {
    String key = id.toString();
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(s3Properties.getBucket())
        .key(key)
        .build();

    return s3Client.getObject(getObjectRequest);
  }

  @Override
  public ResponseEntity<Void> download(BinaryContentDto dto) {
    String key = dto.id().toString();
    String contentType = dto.contentType();

    String presignedUrl = generatePresignedUrl(key, contentType);

    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(presignedUrl))
        .build();
  }

  private String generatePresignedUrl(String key, String contentType) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(s3Properties.getBucket())
        .key(key)
        .responseContentType(contentType)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(getObjectRequest)
        .build();

    return s3Presigner.presignGetObject(presignRequest).url().toString();
  }
}