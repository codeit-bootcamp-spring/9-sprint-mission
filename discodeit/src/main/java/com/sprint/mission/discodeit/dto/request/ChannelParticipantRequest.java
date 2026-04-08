package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ChannelParticipantRequest(
    @NotNull(message = "참여할 사용자 ID는 필수입니다.")
    UUID userId
) {

}