package com.sprint.mission.discordit.dto.request;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    List<UUID> participantIds
) {

}
