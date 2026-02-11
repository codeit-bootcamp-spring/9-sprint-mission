package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.AttachmentType;

public record CreateBinaryContentDto(
        AttachmentType type, String filename, byte[] bytes
) {
}
