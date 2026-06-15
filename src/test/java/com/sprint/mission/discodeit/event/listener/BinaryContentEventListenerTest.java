package com.sprint.mission.discodeit.event.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.message.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BinaryContentEventListenerTest {

  @Mock
  private BinaryContentService binaryContentService;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private BinaryContentEventListener binaryContentEventListener;

  private UUID binaryContentId;
  private BinaryContent binaryContent;
  private byte[] testBytes;
  private BinaryContentCreatedEvent event;

  @BeforeEach
  void setUp() {
    binaryContentId = UUID.randomUUID();
    testBytes = "test content".getBytes();
    binaryContent = new BinaryContent("test.txt", (long) testBytes.length, "text/plain");
    ReflectionTestUtils.setField(binaryContent, "id", binaryContentId);
    
    event = new BinaryContentCreatedEvent(binaryContent, Instant.now(), testBytes);
  }

  @Test
  @DisplayName("파일 업로드 성공 시 상태를 SUCCESS로 업데이트")
  void on_Success() {
    // given
    given(binaryContentStorage.put(binaryContentId, testBytes)).willReturn(binaryContentId);

    // when
    binaryContentEventListener.on(event);

    // then
    verify(binaryContentStorage).put(binaryContentId, testBytes);
    verify(binaryContentService).updateStatus(binaryContentId, BinaryContentStatus.SUCCESS);
  }

  @Test
  @DisplayName("파일 업로드 실패 시 상태를 FAIL로 업데이트")
  void on_StorageFailure() {
    // given
    given(binaryContentStorage.put(binaryContentId, testBytes))
        .willThrow(new RuntimeException("Storage error"));

    // when
    binaryContentEventListener.on(event);

    // then
    verify(binaryContentStorage).put(binaryContentId, testBytes);
    verify(binaryContentService).updateStatus(binaryContentId, BinaryContentStatus.FAIL);
  }

  @Test
  @DisplayName("상태 업데이트 실패 시 저장소에는 저장하지만 상태 업데이트는 실패")
  void on_StatusUpdateFailure() {
    // given
    given(binaryContentStorage.put(binaryContentId, testBytes)).willReturn(binaryContentId);
    given(binaryContentService.updateStatus(eq(binaryContentId), any(BinaryContentStatus.class)))
        .willThrow(new RuntimeException("Update failed"));

    // when
    try {
      binaryContentEventListener.on(event);
    } catch (RuntimeException e) {
      // Exception should be handled within the listener
    }

    // then
    verify(binaryContentStorage).put(binaryContentId, testBytes);
    verify(binaryContentService).updateStatus(binaryContentId, BinaryContentStatus.SUCCESS);
  }
}