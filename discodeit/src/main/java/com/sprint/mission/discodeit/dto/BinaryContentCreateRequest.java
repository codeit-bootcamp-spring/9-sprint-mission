package com.sprint.mission.discodeit.dto;

public record BinaryContentCreateRequest(
        String filename,
        String contentType,
        byte[] bytes
) {}

