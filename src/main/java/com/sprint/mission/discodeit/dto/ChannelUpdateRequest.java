package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record ChannelUpdateRequest(UUID channelId, String name, String description) {}
