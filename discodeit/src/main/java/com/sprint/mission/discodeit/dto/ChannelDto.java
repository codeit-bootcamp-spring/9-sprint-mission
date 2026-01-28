package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ChannelDto {
    // PUBLIC 채널 생성
    record CreatePublicRequest(
            String name,
            String description
    ) {}

    // PRIVATE 채널 생성 (이름/설명 생략, 참여자 정보 필수)
    record CreatePrivateRequest(
            List<UUID> participantUserIds
    ) {}

    // 채널 정보 응답 (최근 메시지 시간 포함)
    record Response(
            UUID id,
            String name,
            String description,
            String type, // PUBLIC or PRIVATE
            Instant lastMessageAt,
            List<UUID> participantUserIds // PRIVATE인 경우만 포함
    ) {}
}