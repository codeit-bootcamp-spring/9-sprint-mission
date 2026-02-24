package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReadStatusCreateRequest {

  @NotNull(message = "사용자 ID는 필수입니다.")
  private UUID userId;

  @NotNull(message = "채널 ID는 필수입니다.")
  private UUID channelId;

  private Instant lastReadAt;
}