package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MessageUpdateRequest(
    @NotNull @NotBlank
    String newContent
) {

}
