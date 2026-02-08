package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record UserStatusUpdateByUserIdRequest(UUID userId, Instant lastSeenAt) {}
