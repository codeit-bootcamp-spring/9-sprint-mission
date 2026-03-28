package com.sprint.mission.discodeit.dto.request;

public record RequestCreateBinaryContentDto(
        String contentType, String filename, byte[] bytes
) {
}
