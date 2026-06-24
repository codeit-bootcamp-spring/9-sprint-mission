package com.sprint.mission.discodeit.event;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SseEvent {

    private UUID targetUserId;
    private String eventType;
    private Object data;
    private UUID channelId;
}