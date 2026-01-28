package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String content;
    private UUID channelId;
    private UUID authorId;


    public Message(String content, UUID channelId, UUID authorId) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
    }

    public void update(String content) { this.content = content; }

    public UUID getId() { return id; }

    public String getContent() { return content; }

    public void setContent(String content) {
        this.content = content;
    }
}