package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(

    @NotNull(message = "userId는 필수입니다.")
    UUID userId,

    @NotNull(message = "lastActiveAt은 필수입니다.")
    @PastOrPresent(message = "lastActiveAt은 현재 또는 과거 시간이어야 합니다.")
    Instant lastActiveAt

) {

}