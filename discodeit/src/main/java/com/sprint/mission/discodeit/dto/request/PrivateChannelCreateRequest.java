package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(

    @NotNull(message = "초대할 사용자 목록은 필수입니다.")
    @Size(min = 1, message = "최소 1 명 이상의 사용자를 초대해야 합니다.")
    List<UUID> participantIds
) {

}
