package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageUpdateRequest(
    @NotBlank(message = "수정할 내용을 입력해주세요.")
    String newContent
) {

}