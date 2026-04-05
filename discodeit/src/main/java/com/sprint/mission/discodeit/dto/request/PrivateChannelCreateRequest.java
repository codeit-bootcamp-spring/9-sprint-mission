package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(

    @NotNull(message = "비공개 채널 ID는 필수입니다.")
    List<UUID> participantIds
) {

}
