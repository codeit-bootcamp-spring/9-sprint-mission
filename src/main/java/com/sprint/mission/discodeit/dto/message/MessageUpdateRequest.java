package com.sprint.mission.discodeit.dto.message;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class MessageUpdateRequest {
    private UUID id;
    private String content;
}
