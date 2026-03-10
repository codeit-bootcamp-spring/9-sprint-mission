package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
    UUID id,
    String type, // PUBLIC, PRIVATE
    String name,
    String description,
    List<UserDto> members, // List<UUID> -> List<UserDto>로 변경
    Instant lastMessageAt
) {

}