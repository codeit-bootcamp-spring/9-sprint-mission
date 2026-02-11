package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 채널 조회 응답 DTO
 * - 요구사항: 마지막 메시지 시간 포함
 * - PRIVATE면 참여 userId 목록 포함
 */
public record ChannelResponse(
        UUID id,
        String name,
        String description,
        boolean isPrivate,
        List<UUID> participantUserIds, // PUBLIC이면 null 가능
        Instant lastMessageAt          // ✅ Long → Instant
) {}


