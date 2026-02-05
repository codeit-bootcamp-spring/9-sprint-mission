package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public class BinaryContentDto {
    public record createDto(
            String fileName,
            String contentType
    ){}
}
