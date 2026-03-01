package com.sprint.mission.discodeit.dto;

public record BinaryContentCreateRequest(
    byte[] bytes,
    String contentType,
    String fileName,
    Long size
) {

}