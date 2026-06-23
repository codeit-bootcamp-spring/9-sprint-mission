package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.sse.SseService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
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
  private final SseService sseService;
  private final BinaryContentMapper binaryContentMapper;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {
    var binaryContent = event.getBinaryContent();
    try {
      binaryContentStorage.put(binaryContent.getId(), event.getBytes());
      binaryContent.updateStatus(BinaryContentStatus.SUCCESS);
      log.info("바이너리 데이터 저장 성공: id={}", binaryContent.getId());
    } catch (Exception e) {
      binaryContent.updateStatus(BinaryContentStatus.FAIL);
      log.error("바이너리 데이터 저장 실패: id={}", binaryContent.getId(), e);
    }
    binaryContentRepository.save(binaryContent);

    BinaryContentDto dto = binaryContentMapper.toDto(binaryContent);
    sseService.broadcast("binaryContents.updated", dto);
  }
}