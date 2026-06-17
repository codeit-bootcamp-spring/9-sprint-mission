package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
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
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;

  @Async("taskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {
    UUID contentId = event.binaryContentId();
    log.info("바이너리 콘텐츠 저장 이벤트 수신 - ID: {}, 파일명: {}",
        event.binaryContentId(), event.fileName());

    try {
      binaryContentStorage.put(event.binaryContentId(), event.fileData());
      log.info("바이너리 콘텐츠 저장 성공 - ID: {}", event.binaryContentId());
      binaryContentService.updateStatus(contentId, BinaryContentStatus.SUCCESS);
    } catch (Exception e) {
      log.error("바이너리 콘텐츠 저장 실패 - ID: {}", event.binaryContentId(), e);
      try {
        binaryContentService.updateStatus(contentId, BinaryContentStatus.FAIL);
      } catch (Exception ex) {
        log.error("바이너리 콘텐츠 상태를 FAIL로 변경하는 중 오류가 추가로 발생했습니다. - ID: {}", contentId, ex);
      }
    }
  }
}