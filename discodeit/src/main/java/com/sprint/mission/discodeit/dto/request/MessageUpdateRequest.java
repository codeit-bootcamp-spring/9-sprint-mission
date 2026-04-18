package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageUpdateRequest(

    @NotBlank(message = "메세지 내용은 필수 입력 값입니다.")
    @Size(max = 100, message = "100자 이하로 입력해주세요")
    String newContent
) {

}
