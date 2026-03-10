package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile, // String에서 BinaryContentDto(객체)로 변경!
    Boolean online
) {

}