package com.sprint.mission.discodeit.dto;

import java.io.InputStream;

public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        byte[] bytes
) {
}