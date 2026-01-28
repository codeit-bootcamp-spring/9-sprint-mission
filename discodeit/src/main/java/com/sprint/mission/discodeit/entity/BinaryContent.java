package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class BinaryContent implements Serializable { // 수정 불가 원칙에 따라 BaseEntity 상속 대신 직접 구현
    private static final long serialVersionUID = 1L;

    private final UUID id;         // 수정 불가이므로 final [cite: 2025-11-18]
    private final Instant createdAt;
    // [규칙] BinaryContent는 updatedAt 필드를 정의하지 않습니다.

    public BinaryContent() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }
}