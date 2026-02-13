package com.sprint.mission.discodeit.dto.binarycontent;

public record BinaryContentCreateRequest(
        Params params
) {
    public record Params(
            byte[] bytes,
            String contentType,
            String fileName
    ) {}
}
