package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentCreatedEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(BinaryContentCreatedEvent event) {
    log.debug("이벤트 수신: BinaryContentCreatedEvent id={}", event.getBinaryContentId());
    Optional<BinaryContent> optional = binaryContentRepository.findById(event.getBinaryContentId());
    if (optional.isEmpty()) {
      log.error("BinaryContent 엔티티를 찾을 수 없음 id={}", event.getBinaryContentId());
      return;
    }
    BinaryContent binaryContent = optional.get();
    try {
      binaryContentStorage.put(binaryContent.getId(), event.getBytes());
      binaryContent.markSuccess();
      binaryContentRepository.save(binaryContent);
      log.info("바이너리 저장 성공: id={}", binaryContent.getId());
    } catch (Exception e) {
      log.error("바이너리 저장 실패: id={}, error={}", binaryContent.getId(), e.getMessage(), e);
      try {
        binaryContent.markFail();
        binaryContentRepository.save(binaryContent);
      } catch (Exception ex) {
        log.error("상태 업데이트 실패: id={}, error={}", binaryContent.getId(), ex.getMessage(), ex);
      }
    }
  }
}


