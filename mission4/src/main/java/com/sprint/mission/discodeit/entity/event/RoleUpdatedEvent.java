package com.sprint.mission.discodeit.entity.event;

import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;

public record RoleUpdatedEvent(
    UUID targetId, Role oldRole, Role newRole
) {

}
