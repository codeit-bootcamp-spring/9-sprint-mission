package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class BinaryContentUploadEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentUploadStatusUpdater statusUpdater;

  @Async("applicationTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(BinaryContentCreatedEvent event) {
    try {
      binaryContentStorage.put(event.binaryContentId(), event.bytes());
      statusUpdater.update(event.binaryContentId(), BinaryContentStatus.SUCCESS);
      log.info("Binary content upload succeeded: binaryContentId={}", event.binaryContentId());
    } catch (RuntimeException ex) {
      statusUpdater.update(event.binaryContentId(), BinaryContentStatus.FAIL);
      log.error("Binary content upload failed: binaryContentId={}", event.binaryContentId(), ex);
    }
  }
}
