package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    String contentType,
    long size,
    String url) {

}
