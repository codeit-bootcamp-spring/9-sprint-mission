package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
    @NotBlank(message = "변경할 채널 이름은 필수입니다.")
    @Size(min = 2, max = 50, message = "채널 이름은 2자 이상 50자 이하여야 합니다.")
    String newName,

    @Size(max = 255, message = "채널 설명은 255자를 초과할 수 없습니다.")
    String newDescription
) {

}