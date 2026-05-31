package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.s3.AwsProperties;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
class S3BinaryContentStorageTest {

  @Autowired
  private S3BinaryContentStorage storage;

  @Autowired
  private AwsProperties awsProperties;

  private final UUID testId = UUID.randomUUID();
  private final byte[] testContent = "Hello S3 Storage!".getBytes(StandardCharsets.UTF_8);
  private final String contentType = "text/plain";

  @Test
  @DisplayName("S3 저장소 전체 프로세스 테스트: 업로드, 다운로드(리다이렉트), 스트림 조회")
  void s3FullProcessTest() throws IOException {
    UUID uploadedId = storage.put(testId, testContent);
    assertThat(uploadedId).isEqualTo(testId);

    try (InputStream is = storage.get(testId)) {
      byte[] downloadedContent = is.readAllBytes();
      assertThat(downloadedContent).isEqualTo(testContent);
    }

    BinaryContentDto dto = new BinaryContentDto(testId, "test", 1024L, contentType);

    ResponseEntity<Void> response = storage.download(dto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);

    String location = response.getHeaders().getLocation().toString();
    assertThat(location).contains("s3." + awsProperties.getRegion() + ".amazonaws.com");
    assertThat(location).contains(awsProperties.getBucket());
    assertThat(location).contains(testId.toString());

    System.out.println("Generated Presigned URL: " + location);
  }
}
