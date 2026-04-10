package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageUpdateRequest(
    @NotBlank(message = "newContent는 필수입니다")
    @Size(max = 2000, message = "newContent는 2000자 이하여야 합니다")
    String newContent
) {

}
