package com.sprint.mission.mission1.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {
    UUID id;
    Long createdAt;
    Long updatedAt;
    String name;
    UUID ownerId;
    List<User> channelUsers;
    List<Message> channelMessage;

    public Channel(UUID id, String name, UUID ownerId) {
        this.id = id;
        this.channelUsers = new ArrayList<>();
        this.channelMessage = new ArrayList<>();
        this.name = name;
        this.ownerId = ownerId;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
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

    public String getName() {
        return name;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public List<User> getChannelUsers() {
        return channelUsers;
    }

    public List<Message> getChannelMessage() {
        return channelMessage;
    }

    public void update(String name) {
        this.updatedAt = System.currentTimeMillis();
        this.name = name;
    }
}