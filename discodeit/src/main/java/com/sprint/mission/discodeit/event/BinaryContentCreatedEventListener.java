package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.sse.SseService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentCreatedEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;
  private final SseService sseService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(BinaryContentCreatedEvent event) {
    try {
      binaryContentStorage.put(event.binaryContentId(), event.bytes());
      BinaryContentDto binaryContent =
          binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
      sseService.broadcast("binaryContents.updated", binaryContent);
      log.debug("Binary content bytes stored: id={}", event.binaryContentId());
    } catch (Exception e) {
      BinaryContentDto binaryContent =
          binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
      sseService.broadcast("binaryContents.updated", binaryContent);
      log.warn("Binary content bytes storage failed: id={}", event.binaryContentId(), e);
    }
  }
}
