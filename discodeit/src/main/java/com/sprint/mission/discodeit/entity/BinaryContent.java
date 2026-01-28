package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

/**
 * 이미지/파일 등 바이너리 데이터 표현
 * - 수정 불가능(immutable)로 가정
 * - updatedAt 없음
 */
@Getter
public class BinaryContent {

    private final UUID id;
    private final long createdAt;

    private final byte[] bytes;

    public BinaryContent(byte[] bytes) {
        this(UUID.randomUUID(), System.currentTimeMillis(), bytes);
    }

    public BinaryContent(UUID id, long createdAt, byte[] bytes) {
        this.id = id;
        this.createdAt = createdAt;
        this.bytes = bytes;
    }
}

