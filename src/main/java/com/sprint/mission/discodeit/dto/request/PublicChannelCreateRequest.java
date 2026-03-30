package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(
    @NotBlank(message = "name은 필수입니다")
    @Size(min = 1, max = 50, message = "name은 1자 이상 50자 이하여야 합니다")
    String name,

    @NotBlank(message = "description은 필수입니다")
    @Size(max = 500, message = "description은 500자 이하여야 합니다")
    String description
) {

}
