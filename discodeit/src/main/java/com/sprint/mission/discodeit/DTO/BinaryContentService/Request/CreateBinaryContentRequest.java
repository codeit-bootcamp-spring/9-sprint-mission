package com.sprint.mission.discodeit.DTO.BinaryContentService;

import com.sprint.mission.discodeit.entity.BinaryContentOwnerType;

import java.util.UUID;

public record CreateBinaryContentRequest(
        BinaryContentOwnerType ownerType,
        UUID ownerId,
        byte[] data
) {
}
