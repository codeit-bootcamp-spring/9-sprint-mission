package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record MessageCreateRequest(

    @NotBlank(message = "메시지 내용은 필수입니다.")
    @Size(max = 1000, message = "메시지는 1000자 이하여야 합니다.")
    String content,

    @NotNull(message = "채널 ID는 필수입니다.")
    UUID channelId,

    @NotNull(message = "작성자 ID는 필수입니다.")
    UUID authorId
) {

}
