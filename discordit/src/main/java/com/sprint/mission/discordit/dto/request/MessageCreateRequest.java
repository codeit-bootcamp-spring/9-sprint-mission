package com.sprint.mission.discordit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record MessageCreateRequest(
    @Schema(description = "메시지 내용")
    String content,

    @Schema(description = "채널ID")
    UUID channelId,

    @Schema(description = "작성자 ID")
    UUID authorId
) {

}
