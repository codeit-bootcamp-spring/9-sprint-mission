package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

/**
 * 채널 조회 응답 DTO
 * - 요구사항: 마지막 메시지 시간 포함(추후 Message/ReadStatus 붙이면 채움)
 * - PRIVATE면 참여 userId 목록 포함
 */
public record ChannelResponse(
        UUID id,
        String name,
        String description,
        boolean isPrivate,
        List<UUID> participantUserIds, // PUBLIC이면 null/empty 가능
        Long lastMessageAt             // 지금은 null 가능(서비스 고도화에서 채움)
) {}

