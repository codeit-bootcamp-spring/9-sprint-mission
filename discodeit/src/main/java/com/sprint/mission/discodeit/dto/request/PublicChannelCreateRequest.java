package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record PublicChannelCreateRequest(
    @NotBlank(message = "채널 이름은 필수입니다.")
    @Size(min = 1, max = 100, message = "채널 이름은 1자 이상 100자 이하로 입력해주세요.")
    String name,

    String description
) {

}