package com.sprint.mission.discodeit.dto.channel;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChannelUpdateRequest {
    private UUID id;
    private String name;
    private String description;
}
