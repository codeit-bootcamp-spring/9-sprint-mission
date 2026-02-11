package com.sprint.mission.discodeit.dto;

public record ChannelUpdateRequest(
        String name,
        String description
) {}

// 수정할 채널이름, 새로운 채널설명