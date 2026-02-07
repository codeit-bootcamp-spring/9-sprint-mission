package com.sprint.mission.discodeit.dto.binarycontent;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class BinaryContentDTO {
    private UUID id;
    private Instant createdAt;
    private byte[] data;
    private UUID userId;
    private UUID messageId;
    private String fileName;
    private String contentType;
}
