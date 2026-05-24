package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.S3BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
@ActiveProfiles("test")
@SpringBootTest(properties = {
    "discodeit.storage.type=s3",
    "discodeit.storage.s3.access-key=dummy-access-key",
    "discodeit.storage.s3.secret-key=dummy-secret-key",
    "discodeit.storage.s3.region=ap-northeast-2",
    "discodeit.storage.s3.bucket=dummy-bucket"
})
class S3BinaryContentStorageTest {

  @Autowired
  private ApplicationContext context;

  @Autowired
  private BinaryContentStorage storage;

  @Test
  @DisplayName("storage.type 속성이 s3일 때 S3BinaryContentStorage가 Bean으로 등록된다")
  void s3StorageBeanLoaded() {
    String[] beanNames = context.getBeanNamesForType(S3BinaryContentStorage.class);

    assertThat(beanNames).hasSize(1);
  }

  @Test
  @DisplayName("BinaryContentStorage 인터페이스의 구현체로 S3BinaryContentStorage가 주입된다")
  void checkStorageImplementation() {
    assertThat(storage).isInstanceOf(S3BinaryContentStorage.class);
  }
}