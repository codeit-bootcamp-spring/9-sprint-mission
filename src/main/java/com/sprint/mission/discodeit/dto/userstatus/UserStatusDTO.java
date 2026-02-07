package com.sprint.mission.discodeit.dto.userstatus;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class UserStatusDTO {
    private UUID id;
    private UUID userId;
    private Instant lastActiveAt;
    private boolean online;
}