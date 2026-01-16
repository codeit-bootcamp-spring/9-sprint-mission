package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private UUID id;
    private String name;
    private ChannelType type;
    private String description;
    private Category category;
    private Long createdAt;
    private Long updatedAt;
    //constructor
    public Channel(String name, ChannelType type, String description, Category category) {
        long now = System.currentTimeMillis();
        this.id = UUID.randomUUID();
        this.createdAt = now;
        this.updatedAt = now;

        this.name = name;
        this.type = type;
        this.description = description;
        this.category = category;
    }
    //설명은 굳이 안써도 됨
    public Channel(String name, ChannelType type, Category category) {
        this(name, type, null, category);
    }

    //getter
    public UUID getId() { return id; }
    public String getName() { return name; }
    public ChannelType getType() { return type; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    //update
    public void update(String name, ChannelType type, String description, Category category) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.category = category;
        this.updatedAt = System.currentTimeMillis();
    }
}