package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @NotNull(message = "Private 채널의 참가자는 필수입니다.")
    List<UUID> participantIds
) {
}
