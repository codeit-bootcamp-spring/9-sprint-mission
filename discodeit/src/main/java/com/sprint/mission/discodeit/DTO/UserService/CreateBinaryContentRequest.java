package com.sprint.mission.discodeit.DTO.UserService;

import com.sprint.mission.discodeit.entity.BinaryContentOwnerType;

import java.util.UUID;

public record CreateBinaryContentRequest(
        BinaryContentOwnerType ownerType,
        UUID ownerId,
        byte[] data
) {
}
