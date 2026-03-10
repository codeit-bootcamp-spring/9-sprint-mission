package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PublicChannelUpdateRequest(
    @NotBlank String newName,        // name -> newName으로 변경
    String newDescription            // description -> newDescription으로 변경
) {

}