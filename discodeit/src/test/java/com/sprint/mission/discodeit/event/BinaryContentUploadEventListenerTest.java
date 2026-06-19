package com.sprint.mission.discodeit.event;

import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BinaryContentUploadEventListenerTest {

  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Mock
  private BinaryContentUploadStatusUpdater statusUpdater;

  @InjectMocks
  private BinaryContentUploadEventListener listener;

  @Test
  @DisplayName("handle 성공: 스토리지 업로드 후 상태를 SUCCESS로 변경한다")
  void handle_success() {
    UUID binaryContentId = UUID.randomUUID();
    byte[] bytes = new byte[]{1, 2, 3};

    listener.handle(new BinaryContentCreatedEvent(binaryContentId, bytes));

    then(binaryContentStorage).should().put(eq(binaryContentId), aryEq(bytes));
    then(statusUpdater).should().update(binaryContentId, BinaryContentStatus.SUCCESS);
  }

  @Test
  @DisplayName("handle 실패: 스토리지 업로드 예외를 잡고 상태를 FAIL로 변경한다")
  void handle_fail_storageError() {
    UUID binaryContentId = UUID.randomUUID();
    byte[] bytes = new byte[]{9, 8};

    willThrow(new RuntimeException("storage error"))
        .given(binaryContentStorage).put(eq(binaryContentId), aryEq(bytes));

    listener.handle(new BinaryContentCreatedEvent(binaryContentId, bytes));

    then(statusUpdater).should().update(binaryContentId, BinaryContentStatus.FAIL);
  }
}
