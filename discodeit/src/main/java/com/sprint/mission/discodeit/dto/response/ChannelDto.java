package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


public record ChannelDto(
    UUID id,
    String type,
    String name,
    String description,
    List<UserDto> participants,
    Instant lastMessageAt
) {

}