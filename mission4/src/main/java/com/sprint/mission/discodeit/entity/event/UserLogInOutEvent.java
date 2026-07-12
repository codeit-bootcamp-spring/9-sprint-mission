package com.sprint.mission.discodeit.entity.event;

import java.util.UUID;

public record UserLogInOutEvent(
    UUID userId,
    boolean isLogin
) {

}
