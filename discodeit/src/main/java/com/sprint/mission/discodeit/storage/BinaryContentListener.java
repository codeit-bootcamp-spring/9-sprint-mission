package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.content.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {
    log.debug("BinaryContentCreatedEvent 수신 완료 - 스토리지 저장 시작: contentId={}",
        event.getBinaryContentId());

    try {
      binaryContentStorage.put(event.getBinaryContentId(), event.getBytes());

      binaryContentService.updateStatus(event.getBinaryContentId(), BinaryContentStatus.SUCCESS);
      log.info("바이너리 데이터 저장 성공 - 메타데이터 SUCCESS 반영 완료: id={}", event.getBinaryContentId());

    } catch (Exception e) {
      log.error("바이너리 데이터 저장 중 예외 발생 - 메타데이터 FAIL 반영 프로세스 진입: id={}", event.getBinaryContentId(),
          e);

      try {
        binaryContentService.updateStatus(event.getBinaryContentId(), BinaryContentStatus.FAIL);
        log.info("바이너리 데이터 저장 실패 - 메타데이터 FAIL 반영 완료: id={}", event.getBinaryContentId());
      } catch (Exception ex) {
        log.error("FAIL 상태 업데이트 중 추가 예외 발생: id={}", event.getBinaryContentId(), ex);
      }
    }
  }
}