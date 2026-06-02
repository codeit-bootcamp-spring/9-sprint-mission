package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentUploadStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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

  // @Recover: @Retryable이 maxAttempts 모두 실패했을 때 자동 호출
  // 첫 번째 파라미터는 반드시 발생한 예외, 이후는 원본 메서드와 동일한 파라미터
  @Recover
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void recover(RuntimeException e, UUID binaryContentId, byte[] bytes) {
    log.error("바이너리 파일 저장 최종 실패: id={}", binaryContentId);

    // status를 FAIL로 업데이트
    binaryContentService.updateStatus(binaryContentId, BinaryContentUploadStatus.FAIL);

    // 디버깅에 필요한 정보 수집
    String requestId = MDC.get("requestId"); // MdcTaskDecorator로 비동기 스레드에 전파된 RequestId
    String failMessage = String.join("\n",
        "실패한 작업: S3 파일 업로드",
        "RequestId: " + requestId,
        "BinaryContentId: " + binaryContentId,
        "Error: " + e.getMessage()
    );

    log.error("관리자 알림 발송:\n{}", failMessage);

    // 관리자에게 알림 생성
    notificationService.createAdminNotification("S3 파일 업로드 실패", failMessage);
  }
}