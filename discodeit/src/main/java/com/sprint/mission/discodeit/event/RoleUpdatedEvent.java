package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.UserRole;
import java.util.UUID;

public record RoleUpdatedEvent(
    UUID userId,
    UserRole oldRole,
    UserRole newRole
) {

}
