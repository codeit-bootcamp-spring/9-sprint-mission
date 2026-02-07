package com.sprint.mission.discodeit.dto.channel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PrivateChannelCreateRequest {
    private List<UUID> participantUserIds;
}
