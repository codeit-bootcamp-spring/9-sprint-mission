package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record BinaryContentResponse(
        UUID id,
        String contentType,
        String fileName,
        Long fileSize,
        byte[] bytes // 프론트엔드에서 base64 변환을 위해 사용
) {}