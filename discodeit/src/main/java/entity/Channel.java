package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {
    private final UUID ownerId;
    private final UUID id;
    private String name;
    private Long createdAt;
    private Long updatedAt;
    private List<User> members;

    public Channel(String name,User owner) {
        this.ownerId = owner.getId();
        this.id = UUID.randomUUID();
        this.name = name;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
        this.members = new ArrayList<>();
        this.members.add(owner);


    }

    public UUID getownerId() {
        return ownerId;
    }

    public UUID getId() {
        return id;
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

    public List<User> getMembers() {
        return members;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "ownerid=" + ownerId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", members=" + members +
                '}';
    }

    public void update(String name) {
        this.name = name;

    }
}



