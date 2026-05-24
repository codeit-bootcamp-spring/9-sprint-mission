package com.sprint.mission.discodeit.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "수정할 Message 내용")
public record MessageUpdateRequest(
    @NotBlank
    String newContent
) {

}
