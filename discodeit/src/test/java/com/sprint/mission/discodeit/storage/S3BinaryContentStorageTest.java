package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.config.S3Config;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = {S3BinaryContentStorage.class, S3Config.class},
    properties = {
        "discodeit.storage.type=s3",

        "discodeit.storage.s3.access-key=${AWS_S3_ACCESS_KEY}",
        "discodeit.storage.s3.secret-key=${AWS_S3_SECRET_KEY}",
        "discodeit.storage.s3.region=${AWS_S3_REGION}",
        "discodeit.storage.s3.bucket=${AWS_S3_BUCKET}",
        "discodeit.storage.s3.presigned-url-expiration=${AWS_S3_PRESIGNED_URL_EXPIRATION:600}"
    }
)

public class S3BinaryContentStorageTest {

  @Autowired
  private BinaryContentStorage s3BinaryContentStorage;

  @Test
  void testPutAndLoad() {
    UUID id = UUID.randomUUID();
    byte[] content = "Finally working test".getBytes();

    s3BinaryContentStorage.put(id, content);

    Resource resource = s3BinaryContentStorage.loadAsResource(id);
    assertThat(resource).isNotNull();
    assertThat(resource.toString()).contains("http");
    assertThat(resource.toString()).contains(id.toString());

    s3BinaryContentStorage.delete(id);
  }
}