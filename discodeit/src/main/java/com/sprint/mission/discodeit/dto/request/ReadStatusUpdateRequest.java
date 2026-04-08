package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;

public record ReadStatusUpdateRequest(

    @PastOrPresent(message = "lastReadAt은 현재 또는 과거 시간이어야 합니다.")
    Instant newLastReadAt

) {

}