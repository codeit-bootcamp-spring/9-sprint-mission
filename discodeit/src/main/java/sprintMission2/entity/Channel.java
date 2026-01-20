package sprintMission2.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String channelName;
    private List<Message> messages;
    private User owner;
    private List<User> member;


    public Channel(String channelName, User owner) {
        this.id = UUID.randomUUID();
        long currentTime = System.currentTimeMillis();
        this.createdAt = currentTime;
        this.updatedAt = currentTime;
        this.channelName = channelName;
        this.messages = new ArrayList<>();
        this.owner = owner;
        this.member = new ArrayList<>();
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

    public String getChannelName() {
        return channelName;
    }

    public List<Message> getMessage() {
        return messages;
    }

    public User getOwner() {
        return owner;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void update(String channelName) {
        this.channelName = channelName;
        this.updatedAt = System.currentTimeMillis();
    }
}
