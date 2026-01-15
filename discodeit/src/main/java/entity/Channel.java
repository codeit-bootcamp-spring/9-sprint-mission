package entity;

import java.util.UUID;

public class Channel {
    private final UUID id;
    private String name;
    private UUID ownerId;
    private final Long createdAt;
    private Long updatedAt;

    public Channel(String name, UUID ownerId) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.ownerId = ownerId;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void update(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

//    @Override
//    public String toString() {
//        return "Channel{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", ownerId=" + ownerId +
//                ", createdAt=" + createdAt +
//                ", updatedAt=" + updatedAt +
//                '}';
//    }
}
