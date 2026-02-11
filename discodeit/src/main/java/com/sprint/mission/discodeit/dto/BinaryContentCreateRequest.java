package com.sprint.mission.discodeit.dto;

public record BinaryContentCreateRequest(
        String fileName,
        byte[] bytes,
        String contentType
) {}
