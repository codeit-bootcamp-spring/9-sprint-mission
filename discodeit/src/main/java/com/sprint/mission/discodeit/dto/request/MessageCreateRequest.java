package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;


public record MessageCreateRequest(
    @NotBlank(message = "메시지 내용은 비어있을 수 없습니다.")
    String content,

    @NotNull(message = "작성자 ID는 필수입니다.")
    UUID authorId,

    @NotNull(message = "채널 ID는 필수입니다.")
    UUID channelId
) {

}