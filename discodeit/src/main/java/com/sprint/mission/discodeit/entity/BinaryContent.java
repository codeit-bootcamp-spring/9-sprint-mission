package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private final Instant createdAt;
    private String url;

    public BinaryContent(String url) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.url = url;
    }
}
