package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    protected UUID id;
    protected Instant createdAt;
    protected Instant updatedAt;

    // [수정] 올바른 Java 생성자 문법으로 변경했습니다. [cite: 2025-11-18]
    public BaseEntity() {
        this.id = UUID.randomUUID();
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // [중요] public으로 선언되어야 UserService 같은 외부 객체에서 호출할 수 있습니다. [cite: 2025-11-18]
    public void recordUpdate() {
        this.updatedAt = Instant.now();
    }
}