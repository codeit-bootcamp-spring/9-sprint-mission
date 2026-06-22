package com.sprint.mission.discodeit.storage.s3;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class S3UploadFailureNotification {

  private final String taskName;    // 실패한 작업 이름
  private final String requestId;   // MDC Request ID
  private final String s3Key;       // 대상 S3 키
  private final String reason;      // 예외 메시지
  private final Instant occurredAt; // 발생 시각
}
