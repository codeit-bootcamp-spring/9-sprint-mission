package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

@Builder
public record MessageCreateRequest(
    @NotBlank(message = "내용은 필수입니다.")
    String content,
    @NotNull(message = "채널 아이디는 필수입니다.")
    UUID channelId,
    @NotNull(message = "메시지를 보내는 사람의 아이디는 필수입니다.")
    UUID authorId
) {

}
