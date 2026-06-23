package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PublicChannelUpdateRequest(
    @NotBlank(message = "채널 이름은 필수입니다.")
    @Schema(description = "채널 이름")
    String newName,
    @NotBlank(message = "채널 정보는 필수입니다.")
    @Schema(description = "채널 정보")
    String newDescription
) {

}
