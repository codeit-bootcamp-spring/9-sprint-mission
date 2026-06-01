package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;

  @Async
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handle(BinaryContentCreatedEvent event) {
    try {
      binaryContentStorage.put(event.binaryContentId(), event.bytes());
      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
      log.info("파일 업로드 완료: id={}", event.binaryContentId());
    } catch (Exception e) {
      log.error("파일 업로드 실패: id={}", event.binaryContentId(), e);
      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
    }
  }
}
