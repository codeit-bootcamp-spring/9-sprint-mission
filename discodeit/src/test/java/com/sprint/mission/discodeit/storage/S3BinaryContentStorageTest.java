package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.config.S3Config;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = {S3BinaryContentStorage.class, S3Config.class},
    properties = {
        "discodeit.storage.type=s3",
        // [.env의 변수명]을 [코드에서 쓰는 이름]에 강제로 꽂아넣습니다.
        "discodeit.storage.s3.access-key=${AWS_S3_ACCESS_KEY}",
        "discodeit.storage.s3.secret-key=${AWS_S3_SECRET_KEY}",
        "discodeit.storage.s3.region=${AWS_S3_REGION}",
        "discodeit.storage.s3.bucket=${AWS_S3_BUCKET}",
        "discodeit.storage.s3.presigned-url-expiration=${AWS_S3_PRESIGNED_URL_EXPIRATION:600}"
    }
)
@TestPropertySource(locations = "file:.env")
public class S3BinaryContentStorageTest {

  @Autowired
  private BinaryContentStorage s3BinaryContentStorage;

  @Test
  void testPutAndLoad() {
    UUID id = UUID.randomUUID();
    byte[] content = "Finally working test".getBytes();

    // 1. 저장
    s3BinaryContentStorage.put(id, content);

    // 2. 로드 및 검증
    Resource resource = s3BinaryContentStorage.loadAsResource(id);
    assertThat(resource).isNotNull();
    assertThat(resource.toString()).contains("http");
    assertThat(resource.toString()).contains(id.toString());

    // 3. 삭제
    s3BinaryContentStorage.delete(id);
  }
}