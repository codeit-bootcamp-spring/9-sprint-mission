package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record UserLogInOutEvent(UUID userId, boolean isLogin) {
}
