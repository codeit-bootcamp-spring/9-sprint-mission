package entity;

import java.util.UUID;

public abstract class BaseUser {

    protected UUID id;
    protected Long createdAt;
    protected Long updatedAt;

    protected BaseUser() {
        this.id = UUID.randomUUID();
//        만든 시간 출력
        this.createdAt = System.currentTimeMillis();
//        바꾼 시간 출력
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }
}
