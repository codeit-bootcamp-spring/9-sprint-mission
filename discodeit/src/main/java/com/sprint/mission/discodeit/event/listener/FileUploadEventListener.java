package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileUploadEventListener {
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleFileUploadEvent(BinaryContentCreatedEvent event) {
    UUID contentId = event.binaryContentId();
    log.debug("비동기 물리 파일 업로드 시작: id={}, fileName={}", contentId, event.fileName());

    try {
      binaryContentStorage.put(contentId, event.fileBytes());

      binaryContentService.updateStatus(contentId, BinaryContentStatus.SUCCESS);

      log.info("비동기 물리 파일 업로드 완료: id={}, fileName={}", contentId, event.fileName());
    } catch (Exception e) {
      binaryContentService.updateStatus(contentId, BinaryContentStatus.FAIL);

      log.error("비동기 물리 파일 업로드 실패: id={}, fileName={}", contentId, event.fileName(), e);
    }
  }
}
