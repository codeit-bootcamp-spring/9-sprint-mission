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

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private String content;
    private List<UUID> attachmentId;
    private final UUID channelId;
    private final UUID authorId;//메시지쓴사람

    public Message(String content, UUID channelId, UUID authorId) {
        this.id = UUID.randomUUID();
        this.attachmentId = new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt=null;
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
    }
    public Message(String content,UUID channelId,UUID authorId,List<UUID> attachmentIds){
        this.id = UUID.randomUUID();
        this.attachmentId = attachmentIds;
        this.createdAt = Instant.now();
        this.updatedAt=null;
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        return String.format("%s 라는내용의 메시지가 있습니다",getContent());
    }

    public void update(String newContent, List<UUID> newAttachmentId) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }


        if(newAttachmentId !=null && !newAttachmentId.equals(this.attachmentId)){
            this.attachmentId=newAttachmentId;
            anyValueUpdated=true;
        }
        if(anyValueUpdated){
            this.updatedAt=Instant.now();
        }
    }
}
