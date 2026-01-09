package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final String id = UUID.randomUUID().toString();
    private long createdAt;
    private long updatedAt;

    // 이름이 아닌 UUID 저장
    private String writerId = "";

    Message(String writerId ){
        this.writerId  = writerId ;

        this.createdAt = this.updatedAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getWriterId () {
        return writerId ;
    }

    public void updateUpdateAt(){
        this.updatedAt = System.currentTimeMillis();
    }

}
