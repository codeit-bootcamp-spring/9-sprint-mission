package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(properties = "discodeit.storage.type=s3")
public class S3BinaryContentStorageTest {

  @Autowired
  private S3BinaryContentStorage storage;

  @Test
  public void 파일_업로드_테스트() {
    UUID testId = UUID.randomUUID();
    byte[] testData = "업로드 테스트 데이터".getBytes();

    UUID savedId = storage.put(testId, testData);

    assertEquals(testId, savedId);
    System.out.println("업로드 성공! UUID: " + savedId);
  }

  @Test
  public void 파일_다운로드_테스트() throws Exception {
    UUID testId = UUID.randomUUID();
    String testText = "다운로드 테스트 데이터";
    byte[] testData = testText.getBytes();
    storage.put(testId, testData);

    InputStream inputStream = storage.get(testId);
    String downloadedText = new String(inputStream.readAllBytes());

    assertEquals(testText, downloadedText);
    System.out.println("다운로드 성공! 내용: " + downloadedText);
  }

  @Test
  public void presigned_url_생성_테스트() {
    UUID testId = UUID.randomUUID();
    byte[] testData = "presigned url 테스트".getBytes();
    storage.put(testId, testData);

    BinaryContentDto dto = new BinaryContentDto(testId, "test.txt",
        (long) testData.length, "text/plain");
    ResponseEntity<? extends Resource> response = storage.download(dto);

    assertEquals(HttpStatus.FOUND, response.getStatusCode());
    assertNotNull(response.getHeaders().getLocation());
    System.out.println("Presigned URL 생성 성공! URL: " + response.getHeaders().getLocation());
  }
}