package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public class UserStatusDTO {

    public record CreateUserStatusDTO(UUID userId) {
    }
    public record UpdateUserStatusDTO(UUID userId) {
    }

}
