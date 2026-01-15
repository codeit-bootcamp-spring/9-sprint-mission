package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {
    private final UUID ownerId;
    private final UUID id;
    private String name;
    private User owner;
    private final Long createdAt;
    private Long updatedAt;
    private List<User> members;

    public Channel(String name,User owner) {
        this.ownerId = owner.getId();
        this.id = UUID.randomUUID();
        this.name = name;
        this.owner = owner;
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
    public User getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return String.format("생성시간:%s,업데이트 시간:%s,채널이름:%s,방장이름:%s",getCreatedAt(),getUpdatedAt(),getName(),getOwner().getUsername());
    }

    public void update(String name) {
        this.name = name;

    }
}



