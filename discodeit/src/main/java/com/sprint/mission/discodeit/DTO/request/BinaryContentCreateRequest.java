package com.sprint.mission.discodeit.DTO.request;

public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        byte[] bytes
) {

}
