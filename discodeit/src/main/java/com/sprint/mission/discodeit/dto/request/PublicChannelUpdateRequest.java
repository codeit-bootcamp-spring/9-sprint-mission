package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
    @Size(min = 1, max = 100, message = "채널 이름은 1자 이상 100자 이하로 입력해야 합니다.")
    String newName,

    @Size(min = 1, max = 500, message = "채널 설명은 1자 이상 500자 이하로 입력해야 합니다.")
    String newDescription
) {

}
