package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

class S3BinaryContentStorageRetryTest {

  @Test
  void put_IsRetryable() throws NoSuchMethodException {
    Method putMethod = S3BinaryContentStorage.class
        .getDeclaredMethod("put", UUID.class, byte[].class);

    Retryable retryable = putMethod.getAnnotation(Retryable.class);

    assertThat(retryable).isNotNull();
    assertThat(retryable.retryFor()).contains(RuntimeException.class);
  }

  @Test
  void recover_IsRecoverMethod() throws NoSuchMethodException {
    Method recoverMethod = S3BinaryContentStorage.class
        .getDeclaredMethod("recover", RuntimeException.class, UUID.class, byte[].class);

    assertThat(recoverMethod.getAnnotation(Recover.class)).isNotNull();
  }
}
