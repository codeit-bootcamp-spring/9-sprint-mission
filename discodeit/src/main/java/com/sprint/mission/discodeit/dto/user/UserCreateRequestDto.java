package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binary.BinaryContentDto;

public record UserCreateRequestDto(
        String username,
        String email,
        String password,
        BinaryContentDto profileImage
) {}
