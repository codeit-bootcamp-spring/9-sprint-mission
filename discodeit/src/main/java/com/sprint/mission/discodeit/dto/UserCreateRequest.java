package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserCreateRequest(
        String username,
        String email,
        String password,
        UUID profileImageId
) {}

// 회원가입할 유저의 이름, 메일주소, 비밀번호, 유저가 설정한 프로필 이미지의 고유id