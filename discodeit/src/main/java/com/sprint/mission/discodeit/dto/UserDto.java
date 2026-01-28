package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public interface UserDto {
    // 유저 생성 요청 (비밀번호 포함, 프로필 이미지 선택)
    record CreateRequest(
            String displayName,
            String email,
            String password,
            String phoneNumber,
            UUID profileId
    ) {}

    // 유저 수정 요청
    record UpdateRequest(
            String displayName,
            String phoneNumber,
            UUID profileId
    ) {}

    // 유저 정보 응답 (비밀번호 제외, 온라인 상태 포함)
    record Response(
            UUID id,
            String displayName,
            String email,
            boolean isOnline,
            UUID profileId
    ) {}
}