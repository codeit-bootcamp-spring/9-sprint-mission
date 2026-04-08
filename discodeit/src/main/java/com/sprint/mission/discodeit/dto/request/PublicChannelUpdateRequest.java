package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record PublicChannelUpdateRequest(
    @NotBlank(message = "새로운 채널 이름은 필수입니다.")
    @Size(min = 1, max = 100)
    String newName,

    String newDescription
) {

}