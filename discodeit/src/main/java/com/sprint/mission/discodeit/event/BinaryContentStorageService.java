package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentUploadStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BinaryContentStorageService {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;
  private final NotificationService notificationService;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Retryable(
      retryFor = RuntimeException.class,
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000, multiplier = 2.0)
  )
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void storeAndUpdateStatus(UUID binaryContentId, byte[] bytes) {
    log.info("바이너리 파일 저장 시작: id={}", binaryContentId);

    binaryContentStorage.put(binaryContentId, bytes);
    binaryContentService.updateStatus(binaryContentId, BinaryContentUploadStatus.SUCCESS);
    log.info("바이너리 파일 저장 성공: id={}", binaryContentId);
  }

  @Recover
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void recover(RuntimeException e, UUID binaryContentId, byte[] bytes) {
    log.error("바이너리 파일 저장 최종 실패: id={}", binaryContentId);

    binaryContentService.updateStatus(binaryContentId, BinaryContentUploadStatus.FAIL);

    String requestId = MDC.get("requestId");

    applicationEventPublisher.publishEvent(new S3UploadFailedEvent(
        requestId,
        binaryContentId,
        e.getMessage()
    ));

    log.error("S3 업로드 최종 실패 이벤트 발행: binaryContentId={}", binaryContentId);
  }
}