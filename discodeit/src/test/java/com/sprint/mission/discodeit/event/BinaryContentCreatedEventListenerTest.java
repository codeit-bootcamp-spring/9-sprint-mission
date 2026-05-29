package com.sprint.mission.discodeit.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.lang.reflect.Method;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@ExtendWith(MockitoExtension.class)
class BinaryContentCreatedEventListenerTest {

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @Test
  void handle_StoresBinaryContentBytes() {
    BinaryContentCreatedEventListener listener =
        new BinaryContentCreatedEventListener(binaryContentStorage);
    UUID binaryContentId = UUID.randomUUID();
    byte[] bytes = "test-data".getBytes();

    listener.handle(new BinaryContentCreatedEvent(binaryContentId, bytes));

    verify(binaryContentStorage).put(binaryContentId, bytes);
  }

  @Test
  void handle_IsExecutedAfterTransactionCommit() throws NoSuchMethodException {
    Method handleMethod = BinaryContentCreatedEventListener.class
        .getDeclaredMethod("handle", BinaryContentCreatedEvent.class);

    TransactionalEventListener annotation =
        handleMethod.getAnnotation(TransactionalEventListener.class);

    assertThat(annotation).isNotNull();
    assertThat(annotation.phase()).isEqualTo(TransactionPhase.AFTER_COMMIT);
  }
}
