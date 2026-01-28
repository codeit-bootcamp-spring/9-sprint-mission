package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

/**
 * 채널 생성 요청 DTO
 * - PUBLIC/PRIVATE 모두 포괄
 * - PRIVATE일 때만 participantUserIds 사용
 */
public record ChannelCreateRequest(
        String name,
        String description,
        boolean isPrivate,
        List<UUID> participantUserIds // PRIVATE일 때만 사용, PUBLIC이면 null/empty 가능
) {}

