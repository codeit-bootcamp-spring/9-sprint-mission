package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(
    @NotBlank(message = "공용채널이름은 필수입니다.")
    @Size(min = 1, max = 100, message = "이름은 1자 이상 100자 이하여야합니다.")
    String name,
    @NotBlank(message = "공용채널설명은 필수입니다.")
    @Size(min = 1, max = 300, message = "설명은 1자 이상 300자 이내여야합니다.")
    String description
) {

}
