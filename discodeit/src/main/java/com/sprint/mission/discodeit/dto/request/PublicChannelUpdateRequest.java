package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
    @NotBlank(message = "변경할 채널 이름을 입력해주세요.")
    @Size(min = 2, max = 50, message = "채널 이름은 2~50자 사이여야 합니다.")
    String newName,

    @Size(max = 255, message = "채널 설명은 255자를 초과할 수 없습니다.")
    String newDescription
) {
}