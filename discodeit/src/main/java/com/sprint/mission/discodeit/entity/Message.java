package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ToString(callSuper = true)
@Getter
public class Message extends BaseEntity implements Serializable{
    @Serial
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
        else{
            this.attachmentIds = new ArrayList<>();
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

    public void updateUpdateAt(){
        this.updatedAt = Instant.ofEpochSecond(System.currentTimeMillis());
    }

}
