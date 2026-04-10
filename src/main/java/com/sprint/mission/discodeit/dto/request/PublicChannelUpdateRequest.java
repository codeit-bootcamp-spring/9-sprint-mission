package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
    @NotBlank(message = "newName은 필수입니다")
    @Size(min = 1, max = 50, message = "newName은 1자 이상 50자 이하여야 합니다")
    String newName,

    @NotBlank(message = "newDescription은 필수입니다")
    @Size(max = 500, message = "newDescription은 500자 이하여야 합니다")
    String newDescription
) {

}
