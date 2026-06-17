package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;


@Builder
public record PublicChannelUpdateRequest(
    @NotBlank(message = "새로운 채널 이름은 필수입니다.")
    @Size(min = 1, max = 100, message = "이름은 1자 이상 100자 이내여야합니다.")
    String newName,
    @NotBlank(message = "새로운 채널 설명은 필수입니다.")
    @Size(min = 1, max = 300, message = "채널 설명은 1자 이상 300자 이내여야합니다.")
    String newDescription
) {

}
