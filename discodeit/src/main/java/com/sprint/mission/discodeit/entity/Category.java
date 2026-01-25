package com.sprint.mission.discodeit.entity;

import java.io.Serializable; // [추가] 직렬화를 위한 임포트
import java.util.UUID;

public class Category implements Serializable { // [추가] Serializable 인터페이스 구현
    // 객체 버전을 관리하기 위한 ID (생략 가능하나 명시하는 것이 좋습니다)
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String name;
    private Long createdAt;
    private Long updatedAt;

    public Category(String name) {
        this.id = UUID.randomUUID();
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
        this.name = name;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }

    public void update(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }
}