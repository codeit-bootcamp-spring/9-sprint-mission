package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public class UserStatusDto {
    public record createUserStatus(
            UUID userId
    ){}
    public record updateUserStatus(
            UUID userId,
            boolean isOnline
    ){}
}
