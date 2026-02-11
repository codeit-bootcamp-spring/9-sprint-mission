package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatus;

public record UserStatusResponse(
        String nickname,
        boolean isOnline,
        String lastActive
) {
    public UserStatusResponse(UserStatus userStatus) {
        this(
                userStatus.getUserId().toString(),
                userStatus.isOnline(),
                userStatus.getUpdatedAt().toString()
        );
    }
}

/* 상태 주인 유저의 식별 정보, 5분이내 활동여부 계산결과, 마지막으로 활동했던 시각을 문자열로 표현
 UserStatus 엔티티를 통째로 입력받아 DTO 체우고 레코드가 기본으로 가지고 있는 생성자를 다시 호출하여
 아래 값을 순서대로 집어넣음.
 유저의 고유 ID를 꺼내서 문자열로 변경해서 담기, 엔티티가 계산한 isOnline의 결과값을 true/false에 담기 
 수정된 시각(Instant)을 문자열로 변환
 */