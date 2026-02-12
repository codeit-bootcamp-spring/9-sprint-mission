package com.sprint.mission.discodeit.dto;

public record BinaryContentCreateRequest(
        String bytes,        // Base64 문자열
        String contentType,  // image/png 등
        String fileName,
        Long fileSize
) {}