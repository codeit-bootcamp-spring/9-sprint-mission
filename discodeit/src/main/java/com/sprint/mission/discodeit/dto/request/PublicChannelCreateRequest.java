package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(
    @NotBlank(message = "Channel name cannot be blank")
    @Size(min = 2, max = 50, message = "Channel name must be between 2 and 50 characters")
    String name,

    @Size(max = 255, message = "Description must be less than 255 characters")
    String description
) {
}
