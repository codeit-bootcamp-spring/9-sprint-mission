package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import java.io.InputStream;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "discodeit.storage.type=s3")
@EnabledIfEnvironmentVariable(named = "AWS_S3_ACCESS_KEY", matches = ".+")
@EnabledIfEnvironmentVariable(named = "AWS_S3_SECRET_KEY", matches = ".+")
class S3BinaryContentStorageTest {

  @Autowired(required = false)
  private S3BinaryContentStorage s3Storage;

  @Test
  void s3StorageBeanShouldBeLoaded() {
    // [검증] 설정이 s3일 때만 빈이 로드되었는지 확인
    assertThat(s3Storage).isNotNull();
  }

  @Test
  void testFullCycle() throws Exception {
    UUID fileId = UUID.randomUUID();
    byte[] data = "Hello S3 Storage with Class Diagram!".getBytes();

    // 1. Put (Save)
    s3Storage.put(fileId, data);

    // 2. Get (Load)
    InputStream inputStream = s3Storage.get(fileId);
    assertThat(inputStream).isNotNull();

    // 3. Download (Presigned URL Redirection)
    BinaryContentDto dto = new BinaryContentDto(
        fileId,
        "sample.txt",
        (long) data.length,
        "text/plain",
        BinaryContentStatus.SUCCESS
    );

    ResponseEntity<Void> response = s3Storage.download(dto);
    assertThat(response.getStatusCode().value()).isEqualTo(302); // 302 리다이렉트 확인
    assertThat(response.getHeaders().getLocation()).isNotNull(); // Location 헤더 확인
    System.out.println("Generated URL: " + response.getHeaders().getLocation());
  }
}
