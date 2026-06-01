package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;

  /**
   * AFTER_COMMIT: 메타 데이터 저장 트랜잭션이 커밋된 후에만 실행됩니다.
   *
   * REQUIRES_NEW: AFTER_COMMIT 시점에는 기존 트랜잭션이 이미 종료되어 있으므로
   * status 업데이트를 위해 새 트랜잭션을 명시적으로 시작합니다.
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {
    log.debug("바이너리 저장 시작: id={}", event.binaryContentId());

    BinaryContent binaryContent = binaryContentRepository.findById(event.binaryContentId())
        .orElseThrow(() -> new IllegalStateException(
            "BinaryContent를 찾을 수 없습니다: " + event.binaryContentId()));

    try {
      binaryContentStorage.put(event.binaryContentId(), event.bytes());
      binaryContent.updateStatus(BinaryContentStatus.SUCCESS);
      log.info("바이너리 저장 완료: id={}", event.binaryContentId());
    } catch (Exception e) {
      binaryContent.updateStatus(BinaryContentStatus.FAIL);
      log.error("바이너리 저장 실패: id={}", event.binaryContentId(), e);
    }
  }
}