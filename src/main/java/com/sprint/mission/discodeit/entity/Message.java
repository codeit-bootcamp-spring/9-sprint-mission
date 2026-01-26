//메세지는 뭘 구현해야할까
//하나하나 생각해보자 일단 ID는 필요하지 그리고 메세지를 쓴 사람
//어떤 채널에서 보냈는지 메세지 내용
//생성, 수정시간
//종합해보면 UUID ID, User ID, Channel ID, content, CreatedAt, UpdatedAt이렇게?

package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID Id;
    private String content;
    private final UUID userId;
    private final UUID channelId;
    private final Long createdAt;
    private Long updatedAt;

    public Message(String content, UUID userId, UUID channelId){
        this.Id = UUID.randomUUID();
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }
    public UUID getId() {return Id;}
    public UUID getUserId() {return userId;}
    public UUID getChannelId() {return channelId;}
    public String getContent() {return content;}
    public Long getCreatedAt() {return createdAt;}
    public Long getUpdatedAt() {return updatedAt;}

    public void update(String content) {
        if(content != null) this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

}