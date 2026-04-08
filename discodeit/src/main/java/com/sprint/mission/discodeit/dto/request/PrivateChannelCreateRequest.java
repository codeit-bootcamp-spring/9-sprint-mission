package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;


public record PrivateChannelCreateRequest(
    @NotEmpty(message = "참여자 목록은 비어있을 수 없습니다.")
    List<UUID> participantIds
) {

}