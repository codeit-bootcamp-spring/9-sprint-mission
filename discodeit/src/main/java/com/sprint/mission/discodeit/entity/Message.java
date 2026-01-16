package com.sprint.mission.discodeit.entity;

import java.util.UUID;


public class Message {

    private UUID id;
    private String content;    // 메시지 내용
    private UUID userId;       // 보낸 사람의 고유 번호 (ID 참조)
    private UUID channelId;    // 이 메시지가 흐르는 통로의 고유 번호 (ID 참조)
    private Long createdAt;
    private Long updatedAt;
    //constructor
    public Message(String content, UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;

        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }
    //getter
    public UUID getId() { return id; }
    public String getContent() { return content; }
    public UUID getUserId() { return userId; }
    public UUID getChannelId() { return channelId; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    //update
    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }
}