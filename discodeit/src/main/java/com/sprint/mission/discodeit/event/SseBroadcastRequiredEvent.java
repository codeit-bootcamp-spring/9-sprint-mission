package com.sprint.mission.discodeit.event;

public record SseBroadcastRequiredEvent(
    String eventName,
    Object data
) {

}
