package com.sprint.mission.discordit.dto.request;

public record PublicChannelUpdateRequest(
    String newName,
    String newDescription
) {

}
