package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;
import java.util.UUID;

public record RequestUpdateUserStatusDto(UUID id, String name, Instant time) {
}
