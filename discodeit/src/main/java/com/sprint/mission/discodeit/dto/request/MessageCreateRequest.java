package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageCreateRequest(
    @NotBlank(message = "Message content cannot be blank")
    String content,

    @NotNull(message = "Channel ID cannot be null")
    UUID channelId,

    @NotNull(message = "Author ID cannot be null")
    UUID authorId
) {
}
