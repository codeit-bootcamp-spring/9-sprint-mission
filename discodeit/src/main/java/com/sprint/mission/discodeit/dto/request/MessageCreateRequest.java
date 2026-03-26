package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record MessageCreateRequest(
    @NotBlank(message = "메세지 내용은 필수 입력 값입니다.")
    @Size(max = 100, message = "100자 이하로 입력해주세요")
    String content,

    @NotNull(message = "채널ID 입력은 필수 입니다")
    UUID channelId,

    @NotNull(message = "유저ID 입력은 필수 입니다")
    UUID authorId
) {

}
