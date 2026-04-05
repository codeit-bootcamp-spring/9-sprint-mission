package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(

    @NotBlank(message = "공개 채널 name은 필수입니다.")
    @Size(min = 2, max = 20, message = "공개 채널 name은 2~20자여야 합니다.")
    String newName,

    @NotBlank(message = "채널 설명은 필수입니다.")
    @Size(max = 1000, message = "채널 설명은 1000자 이하여야 합니다.")
    String newDescription
) {

}
