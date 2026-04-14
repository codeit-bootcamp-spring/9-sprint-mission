package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PublicChannelCreateRequest(
    @NotNull @NotBlank
    @Schema(description = "채널명")
    String name,

    @Schema(description = "채널 설명")
    String description
) {

}
