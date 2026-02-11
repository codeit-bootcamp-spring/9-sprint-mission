package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserStatusCreateRequest(
        UUID userId,
        String type
) {
}
//상태를 생성할 대상 유저의 id, 초기 상태값(ex OFFLINE)