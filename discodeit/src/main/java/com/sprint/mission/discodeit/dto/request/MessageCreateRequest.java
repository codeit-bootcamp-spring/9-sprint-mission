package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record MessageCreateRequest(

    @NotBlank(message = "메시지 내용을 입력해주세요.")
    @Size(max = 2000, message = "메시지는 2000자를 초과할 수 없습니다.")
    String content,

    @NotNull(message = "채널 ID가 누락되었습니다.")
    UUID channelId,

    @NotNull(message = "작성자 ID가 누락되었습니다.")
    UUID authorId

) {
}