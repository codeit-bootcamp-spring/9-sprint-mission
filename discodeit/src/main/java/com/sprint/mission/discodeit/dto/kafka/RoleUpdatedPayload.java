package com.sprint.mission.discodeit.dto.kafka;

import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;

public record RoleUpdatedPayload(UUID userId, Role oldRole, Role newRole) {

}
