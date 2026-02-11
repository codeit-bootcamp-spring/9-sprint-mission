package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private final UUID authorId;
    private final UUID channelId;
    private String content = "";
    private List<UUID> attachmentIds;

    public Message(UUID channelId, UUID authorId, String content, List<UUID> attachmentIds){
        super();
        this.authorId  = authorId;
        this.channelId = channelId;
        this.content = content;

        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            this.attachmentIds = attachmentIds;
        }

        System.out.println("Message 생성 - " + this.toString());
    }

    public void updateContent(String content) {
        this.content = content;
        updateUpdateAt();
    }

    public void addAttachment(List<UUID> attachmentIds){
        this.attachmentIds = attachmentIds;
    }

    public String toString(){
        String createAtToString = this.createdAt
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return "MESSAGE) UUID: " + this.id + " | Content: " + this.content + " | CreateAt: " + createAtToString;
    }
}
