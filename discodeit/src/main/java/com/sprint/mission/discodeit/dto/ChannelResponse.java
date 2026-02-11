package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
        UUID id,
        ChannelType type,
        String name,
        String description,
        Instant lastMessageAt,  // 가장 최근 메시지 시간
        List<UUID> participantIds // (비공개 채널인 경우) 참여자 ID 목록
) {}