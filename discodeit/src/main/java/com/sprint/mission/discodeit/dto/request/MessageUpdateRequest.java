package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Size;

public record MessageUpdateRequest(

    @Size(min = 1, max = 1000, message = "메시지는 1~1000자입니다.")
    String newContent

) {

}