package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private final UUID user;
    private String name;
    private String message;
    private Long createdAt;
    private Long updateAt;

    public Message(String name, String message, UUID user) {
        this.user = user;
        this.name = name;
        this.message = message;
        Long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updateAt = now;
    }

    public UUID getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public Message updateMessageIfUser(UUID userUUID, String newMessage) {
        if (this.user.equals(userUUID)) {
            this.message = newMessage;
            this.updateAt = System.currentTimeMillis();
            return this;
        }
        return null;
    }
    @Override
    public String toString() {
        return "Message{" +
                "name='" + name + '\'' + " " +
                "message='" + message + '\'' +
                '}';
    }
}

