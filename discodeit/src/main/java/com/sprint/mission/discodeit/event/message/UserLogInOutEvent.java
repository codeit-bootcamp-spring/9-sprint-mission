package com.sprint.mission.discodeit.event.message;

import java.util.UUID;

public record UserLogInOutEvent(
    UUID userId,
    boolean isLogin
) {
}
