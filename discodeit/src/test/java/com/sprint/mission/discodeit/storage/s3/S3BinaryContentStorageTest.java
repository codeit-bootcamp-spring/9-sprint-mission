package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(properties = "discodeit.storage.type=s3")
class S3BinaryContentStorageTest {

  @BeforeAll
  static void setupEnv() {
    Dotenv dotenv = Dotenv.configure()
        .ignoreIfMissing()
        .load();

    dotenv.entries().forEach(entry ->
        System.setProperty(entry.getKey(), entry.getValue())
    );
  }

  @Autowired(required = false)
  private S3BinaryContentStorage s3Storage;

  @Test
  @DisplayName("S3 저장소 빈 등록 확인 - STORAGE_TYPE이 s3일 때만 등록되어야 함")
  void beanRegistrationTest() {
    // application.yaml이나 환경변수에 discodeit.storage.type=s3가 설정되어 있어야 합니다.
    assertThat(s3Storage).isNotNull();
  }

  @Test
  @DisplayName("S3 파일 업로드 및 다운로드 스트림 확인")
  void putAndGetTest() throws Exception {
    // Given
    UUID fileId = UUID.randomUUID();
    String content = "Hello S3 Storage High-Level Test";
    byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

    // When: 업로드
    s3Storage.put(fileId, bytes);

    // Then: 다운로드(스트림) 및 내용 검증
    try (InputStream is = s3Storage.get(fileId)) {
      String downloadedContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
      assertThat(downloadedContent).isEqualTo(content);
    }
  }

  @Test
  @DisplayName("download 메서드 호출 시 Presigned URL로 리다이렉트 응답을 반환해야 함")
  void downloadRedirectTest() {
    // Given
    UUID fileId = UUID.randomUUID();
    BinaryContentDto dto = new BinaryContentDto(
        fileId, "Avata.png", 5532L, "image/png"
    );

    // When
    ResponseEntity<?> response = s3Storage.download(dto);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND); // 302 Redirect
    assertThat(response.getHeaders().getLocation()).isNotNull();

    String redirectUrl = response.getHeaders().getLocation().toString();
    System.out.println("Generated Presigned URL for Redirect: " + redirectUrl);

    // URL에 버킷명과 해당 파일의 UUID가 포함되어 있는지 확인
    assertThat(redirectUrl).contains(fileId.toString());
  }
}