package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    private String content;
    private UUID userId;
    private UUID channelId;

    public Message (String content, UUID userId, UUID channelId){
        if(content == null || content.isBlank()){
            throw new IllegalArgumentException("빈 메세지는 전송할 수 없습니다.");
        }
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }

    public void update(String content){
        this.content = content;
        this.setUpdatedAt(System.currentTimeMillis());
    }

    public String getContent() {
        return content;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }
}
