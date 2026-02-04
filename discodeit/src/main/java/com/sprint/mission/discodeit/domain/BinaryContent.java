package com.sprint.mission.discodeit.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class BinaryContent {
        private UUID id;
        private byte[] data;
        private String fileName;
        private UUID userId;
        private UUID messageId;
        private Instant createdAt;
}

