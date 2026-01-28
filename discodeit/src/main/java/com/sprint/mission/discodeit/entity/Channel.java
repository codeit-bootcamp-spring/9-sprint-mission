package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class Channel extends BaseEntity {
    private String name;
    private ChannelType type;
    private String description;
    private Category category;

    public Channel(String name, ChannelType type, String description, Category category) {
        super();
        this.name = name;
        this.type = type;
        this.description = description;
        this.category = category;
    }

    public Channel(String name, ChannelType type, Category category) {
        this(name, type, null, category);
    }

    public void update(String name, ChannelType type, String description, Category category) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.category = category;
        recordUpdate();
    }
}