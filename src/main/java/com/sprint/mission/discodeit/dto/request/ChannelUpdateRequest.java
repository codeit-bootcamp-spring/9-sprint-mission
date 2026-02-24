package com.sprint.mission.discodeit.dto.request;

public record ChannelUpdateRequest(
    String newName,
    String newDescription
) {
}
