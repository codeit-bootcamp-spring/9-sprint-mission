package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;

/**
 * 사용자의 권한(Role)이 변경되었을 때 발행되는 이벤트.
 */
public record RoleUpdatedEvent(UUID userId, Role oldRole, Role newRole) {

}
