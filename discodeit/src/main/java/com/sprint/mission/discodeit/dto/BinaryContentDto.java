package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public interface BinaryContentDto {
    record CreateRequest(
            byte[] bytes,        // 실제 파일 데이터
            String contentType,  // 파일 형식 (image/png 등)
            String fileName,     // 파일 이름
            Long fileSize
    ) {}

    record Response(
            UUID id,
            String contentType,
            String fileName,
            Long fileSize
    ) {}
}