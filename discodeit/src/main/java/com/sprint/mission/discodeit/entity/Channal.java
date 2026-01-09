package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channal {
    private final String id = UUID.randomUUID().toString();
    private long createdAt;
    private long updatedAt;

    private String name = "";

    Channal(String name){
        this.name = name;

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

    public String getName() {
        return name;
    }

    public void updateUpdateAt(){
        this.updatedAt = System.currentTimeMillis();
    }

    public void UpdateName(String name){
        this.name = name;
        this.updateUpdateAt();
    }
}
