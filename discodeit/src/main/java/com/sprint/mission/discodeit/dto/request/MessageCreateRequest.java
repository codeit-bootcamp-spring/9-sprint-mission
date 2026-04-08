package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record MessageCreateRequest(

    @NotBlank(message = "메시지 내용은 필수입니다.")
    @Size(max = 1000, message = "메시지는 최대 1000자입니다.")
    String content,

    @NotNull(message = "channelId는 필수입니다.")
    UUID channelId,

    @NotNull(message = "authorId는 필수입니다.")
    UUID authorId

) {

}