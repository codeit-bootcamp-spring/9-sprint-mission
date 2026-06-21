package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.message.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class BinaryContentEventListener {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(BinaryContentCreatedEvent event) {
    BinaryContent binaryContent = event.getData();
    try {
      binaryContentStorage.put(
          binaryContent.getId(),
          event.getBytes()
      );
      binaryContentService.updateStatus(
          binaryContent.getId(), BinaryContentStatus.SUCCESS
      );
    } catch (RuntimeException e) {
      binaryContentService.updateStatus(
          binaryContent.getId(), BinaryContentStatus.FAIL
      );
    }
  }
}
