package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatus;

public record UserStatusResponse(
        String userId,
        String nickname,
        boolean isOnline,
        String lastActive
) {
    public UserStatusResponse(UserStatus userStatus) {
        this(
                userStatus.getUserId().toString(),
                "haha",
                userStatus.isOnline(),
                userStatus.getLastActiveAt() != null ? userStatus.getLastActiveAt().toString() : "기록 없음"
        );
    }
}
