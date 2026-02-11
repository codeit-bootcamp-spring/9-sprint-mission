package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record ChannelCreateRequest(
        String name,
        String description,
        List<UUID> memberIds
) {}

//채널이름, 채널 설명, 채널 유저의 고유ID 리스트