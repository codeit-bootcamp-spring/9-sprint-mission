package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.BinaryUploadFailedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;
  private final ApplicationEventPublisher eventPublisher;

  @Async("taskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {
    try {
      binaryContentStorage.put(event.contentId(), event.bytes());

      binaryContentService.updateStatus(event.contentId(), BinaryContentStatus.SUCCESS);

    } catch (Exception e) {
      log.error("업로드 실패: contentId={}", event.contentId(), e);

      binaryContentService.updateStatus(event.contentId(), BinaryContentStatus.FAIL);

      String requestId = org.slf4j.MDC.get("requestId");
      if (requestId == null) {
        requestId = "N/A";
      }

      eventPublisher.publishEvent(new BinaryUploadFailedEvent(
          event.contentId(),
          requestId,
          e.getMessage()
      ));
    }
  }
}
