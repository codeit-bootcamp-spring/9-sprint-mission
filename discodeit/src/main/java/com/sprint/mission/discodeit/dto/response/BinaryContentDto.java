// 14. BinaryContentDto.java
package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record BinaryContentDto(UUID id, String fileName, String contentType, Long size) {

}