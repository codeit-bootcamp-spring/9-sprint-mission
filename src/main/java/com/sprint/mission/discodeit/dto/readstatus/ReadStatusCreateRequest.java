package com.sprint.mission.discodeit.dto.readstatus;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class ReadStatusCreateRequest {
    private UUID userId;
    private UUID channelId;
}