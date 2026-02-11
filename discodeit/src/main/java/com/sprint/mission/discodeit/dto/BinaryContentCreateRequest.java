package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record BinaryContentCreateRequest(
        String fileName,
        byte[] data,
        UUID userId,    // 프로필 이미지인 경우 사용
        UUID messageId  // 메시지 첨부파일인 경우 사용
) {}