package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
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
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {
    log.info("스토리지 저장 이벤트 수신 - 파일 ID: {}", event.binaryContentId());
    try {
      binaryContentStorage.put(event.binaryContentId(), event.bytes());
      // 성공하면 SUCCESS로 상태 업데이트
      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
      log.info("스토리지 저장 성공 - 파일 ID: {}", event.binaryContentId());
    } catch (Exception e) {
      // 실패하면 FAIL로 상태 업데이트
      log.error("스토리지 저장 실패 - 파일 ID: {}", event.binaryContentId(), e);
      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
    }
  }
}