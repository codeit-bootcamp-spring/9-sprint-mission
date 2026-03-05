package com.sprint.mission.discodeit.dto.data;

public record BinaryContentDto(
    String fileName,
    Long size,
    String contentType
) {

}
