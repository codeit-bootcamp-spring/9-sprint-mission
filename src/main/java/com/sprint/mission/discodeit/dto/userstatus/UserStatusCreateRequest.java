package com.sprint.mission.discodeit.dto.userstatus;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class UserStatusCreateRequest {
    private UUID userId;
}
