package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.Disabled;
import org.springframework.core.io.Resource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Disabled
@SpringBootTest(properties = "discodeit.storage.type=s3")
public class S3BinaryContentStorageTest {

  static {
    try {
      Properties props = new Properties();
      props.load(new FileInputStream(".env"));
      props.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));
    } catch (Exception e) {
      System.out.println(".env 파일을 읽는 중 오류 발생: " + e.getMessage());
    }
  }

  @Autowired
  private S3BinaryContentStorage storage;

  @Test
  public void s3FullProcessTest() throws Exception {
    // 데이터 준비
    UUID testId = UUID.randomUUID();
    String testText = "S3BinaryContentStorage 통합 테스트 진행 중입니다.";
    byte[] testData = testText.getBytes();
    // 업로드(put) 검증
    UUID savedId = storage.put(testId, testData);
    assertEquals(testId, savedId);
    System.out.println("업로드 테스트 성공! 파일명(UUID): " + savedId);
    // 다운로드(get) 검증
    InputStream inputStream = storage.get(testId);
    String downloadedText = new String(inputStream.readAllBytes());
    assertEquals(testText, downloadedText);
    System.out.println("데이터 읽기 테스트 성공! 내용: " + downloadedText);
    // 임시 주소 발급(download 리다이렉트) 검증
    BinaryContentDto dto = new BinaryContentDto(testId, "test.txt", (long) testData.length,
        "text/plain");
    ResponseEntity<? extends Resource> response = storage.download(dto);
    // 302(FOUND) 정상 설정 체크
    assertEquals(HttpStatus.FOUND, response.getStatusCode());
    // 헤더에 임시 URL값 체크
    assertNotNull(response.getHeaders().getLocation());
    System.out.println("임시 주소 생성 성공! 주소: " + response.getHeaders().getLocation());
  }
}
