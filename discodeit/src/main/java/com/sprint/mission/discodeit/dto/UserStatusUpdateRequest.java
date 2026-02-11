package com.sprint.mission.discodeit.dto;

public record UserStatusUpdateRequest(
        String type
) {
}

//변경하고자 하는 새로운 상태를 문자열로