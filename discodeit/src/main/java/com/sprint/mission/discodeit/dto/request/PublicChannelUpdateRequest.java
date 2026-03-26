package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(

    @Size(min = 1, max = 50, message = "채널 이름은 1~50자입니다.")
    String newName,

    @Size(max = 200, message = "채널 설명은 최대 200자입니다.")
    String newDescription

) {

}