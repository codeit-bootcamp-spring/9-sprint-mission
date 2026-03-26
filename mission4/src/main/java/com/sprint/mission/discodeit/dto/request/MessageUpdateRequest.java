package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MessageUpdateRequest(
    @NotBlank(message = "수정할 메시지 내용은 필수입니다.")
    String newContent
) {

}
