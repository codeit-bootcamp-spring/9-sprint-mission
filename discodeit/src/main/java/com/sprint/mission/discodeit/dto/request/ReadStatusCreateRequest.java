package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record ReadStatusCreateRequest(
    @NotBlank(message = "채널 ID는 필수입니다.")
    UUID channelId,
    @NotBlank(message = "사용자 ID는 필수입니다.")
    UUID userId
) {
}
