package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public class UserServiceResponseDTO {

    public record FindUserId(
            UUID userId,
            boolean isOnline,
            Long lastUpdatedAt
    ){}
}
