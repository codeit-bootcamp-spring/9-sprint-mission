package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binary.BinaryContentDto;

// DTO는 스스로 판단하거나 행동하지 않고, 정보만 전달한다.
public record UserCreateRequestDto (
        String username,
        String email,
        String password,
        BinaryContentDto profileImage
)
{
    }