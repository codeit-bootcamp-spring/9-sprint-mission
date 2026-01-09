package entity;

import java.util.UUID;

public abstract class Leadclass {

    protected UUID id;
    protected Long createdAt;
    protected Long updatedAt;

    protected Leadclass() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = null;
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
