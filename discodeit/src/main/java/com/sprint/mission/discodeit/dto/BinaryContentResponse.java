package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record BinaryContentResponse(
        UUID id,
        String fileName,
        int size, // 파일 크기 정보 제공 (편의상 추가)
        UUID userId,
        UUID messageId
) {}
