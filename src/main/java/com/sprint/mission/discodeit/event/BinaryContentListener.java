package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BinaryContentListener {
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(BinaryContentCreatedEvent event) {
        UUID contentId = event.binaryContentId();
        try {
            binaryContentStorage.put(contentId, event.bytes());
            binaryContentService.updateStatus(contentId, BinaryContentStatus.SUCCESS);
        } catch (Exception e) {
            binaryContentService.updateStatus(contentId, BinaryContentStatus.FAIL);
        }
    }

}

