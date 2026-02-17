package com.sprint.mission.discodeit.dto.readstatus;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class ReadStatusUpdateRequest {
    private UUID id;
    private Instant lastReadAt;
}