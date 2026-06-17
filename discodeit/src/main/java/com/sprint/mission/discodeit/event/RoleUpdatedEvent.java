package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;

public record RoleUpdatedEvent(
    User user,       // 권한이 바뀐 사용자
    Role oldRole,    // 이전 권한
    Role newRole     // 새 권한
) {
}