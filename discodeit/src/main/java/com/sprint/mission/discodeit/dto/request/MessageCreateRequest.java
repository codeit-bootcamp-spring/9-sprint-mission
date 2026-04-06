package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageCreateRequest(
    @NotNull @NotBlank
    @Schema(description = "메시지 내용")
    String content,

    @NotNull
    @Schema(description = "채널")
    UUID channelId,

    @NotNull
    @Schema(description = "작성자")
    UUID authorId
) {

}
