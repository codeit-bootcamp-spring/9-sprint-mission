package com.sprint.mission.discodeit.dto.userstatus;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
import java.time.Instant;

@Getter
@Setter
public class UserStatusUpdateRequest {
    private UUID id;
    private Instant lastActiveAt;
}