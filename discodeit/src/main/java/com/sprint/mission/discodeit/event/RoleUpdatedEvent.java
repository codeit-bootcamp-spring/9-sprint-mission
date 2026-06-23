package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleUpdatedEvent {
  private final UUID userId;
  private final Role oldRole;
  private final Role newRole;

  public RoleUpdatedEvent(UUID userId, Role oldRole, Role newRole) {
    this.userId = userId;
    this.oldRole = oldRole;
    this.newRole = newRole;
  }
}
