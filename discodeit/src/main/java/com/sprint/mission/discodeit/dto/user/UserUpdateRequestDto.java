package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binary.BinaryContentDto;

public record UserUpdateRequestDto(
        String username,
        String email,
        String password,
        BinaryContentDto profileImage
) {}
