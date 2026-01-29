package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String content;
    private List<UUID> attachmentlds;
    private UUID channelId;
    private UUID authorId;

    public Message(String content, UUID channelId, UUID authorId) {
        this.id = UUID.randomUUID();
        this.attachmentlds = new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt=null;
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
    }


    public void update(String newContent) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
