package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Size;

public record ChannelUpdateRequest(
    @Size(min = 2, max = 15,  message = "채널 이름은 2자 이상 15자 이하여야 합니다.")
    String name,
    @Size(min = 2, max = 40,  message = "채널 설명은 2자 이상 40자 이하여야 합니다.")
    String description
) {
}
