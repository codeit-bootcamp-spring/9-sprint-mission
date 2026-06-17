package com.sprint.mission.discodeit.listener;


import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BasicBinaryContentService basicBinaryContentService;

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {
    log.info(">>>> 리스너 동작 시작: ID={}, 바이트 크기={}", event.binaryContentId(), event.bytes().length);
    try {
      binaryContentStorage.put(event.binaryContentId(), event.bytes());
      log.info(">>>> storage.put 완료");
      basicBinaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
      log.info("파일 저장 성공: BinaryContentId- {}", event.binaryContentId());
    } catch (Exception e) {
      basicBinaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
      log.error("파일 저장 실패 : BinaryContentId - {} , 원인 -{}", event.binaryContentId(), e.getMessage(),
          e);
    }

  }
}
