package com.sprint.mission.discodeit.dto.request;

public record BinaryContentCreateRequest(
    byte[] bytes,
    String fileName,
    String contentType,
    Long size
) {

}