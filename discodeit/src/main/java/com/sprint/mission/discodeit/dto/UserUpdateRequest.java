package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserUpdateRequest(
        String name,
        String email,
        String password,
        UUID profileImageId
) {}

//수절할 새 이름, 메일, 비밀번호, 변경할 새로운 프로필 이미지id