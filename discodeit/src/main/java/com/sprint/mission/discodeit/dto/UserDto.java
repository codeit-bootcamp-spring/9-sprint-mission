package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public interface UserDto {
    record CreateRequest(
            String displayName,
            String email,
            String password,
            String phoneNumber,
            UUID profileId
    ) {}

    record UpdateRequest(
            String displayName,
            String email,
            String phoneNumber,
            UUID profileId
    ) {}

    record Response(
            UUID id,
            String displayName,
            String email,
            boolean isOnline,
            UUID profileId,
            String phoneNumber
    ) {}
}