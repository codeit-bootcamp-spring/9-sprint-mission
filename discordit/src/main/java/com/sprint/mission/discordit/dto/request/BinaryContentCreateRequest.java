package com.sprint.mission.discordit.dto.request;

public record BinaryContentCreateRequest(
    String fileName,
    String contentType,
    byte[] bytes
) {

}
