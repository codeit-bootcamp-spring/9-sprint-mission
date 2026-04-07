package com.sprint.mission.discodeit.storage.s3;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test/s3")
@RequiredArgsConstructor
public class S3Controller {

  private final AWSS3Test s3Service;

  @GetMapping("/all")
  public Map<String, Object> runAllTests() {
    String key = "docker-test/combined-test.txt";
    String content = "Full S3 API Test from Docker!";

    try {
      s3Service.upload(key, content.getBytes(), "text/plain");

      byte[] downloaded = s3Service.download(key);
      String downloadedStr = new String(downloaded);

      String presignedUrl = s3Service.generatePresignedUrl(key, 10);

      return Map.of(
          "status", "success",
          "uploadedKey", key,
          "downloadedContent", downloadedStr,
          "presignedUrl", presignedUrl
      );
    } catch (Exception e) {
      return Map.of("status", "error", "message", e.getMessage());
    }
  }

}
