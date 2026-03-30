package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @NotEmpty(message = "참가자 Id는 1개 이상이어야합니다.")
    List<@NotNull(message = "참가자 Id는 비어있을 수 없습니다.") UUID> participantIds
) {

}
