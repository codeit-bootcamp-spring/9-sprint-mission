package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserFinder(UUID id, String name, String userInfo, UUID profileId) {
}
