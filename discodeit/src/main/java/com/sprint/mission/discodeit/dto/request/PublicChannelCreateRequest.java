package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Size(min = 1, max = 12, message = "이름은 1~12자 사이여야 합니다.")
    String name,

    @Size(max = 100, message = "100자 이하로 입력해주세요")
    String description
) {

}
