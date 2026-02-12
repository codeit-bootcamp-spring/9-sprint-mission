package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
public class Channel extends BaseEntity {
    private String name;
    private ChannelType type;
    private String description;
    private boolean isPrivate;

    private List<UUID> participantUserIds = new ArrayList<>();

    public Channel(String name, ChannelType type, String description, boolean isPrivate) {
        super();
        this.name = name;
        this.type = type;
        this.description = description;
        this.isPrivate = isPrivate;
    }

    public Channel(String name, ChannelType type, boolean isPrivate) {
        this(name, type, null, isPrivate);
    }

    public void update(String name, String description) {
        if (this.isPrivate) {
            throw new IllegalStateException("PRIVATE 채널 정보는 수정할 수 없습니다.");
        }
        this.name = name;
        this.description = description;
        recordUpdate();
    }
}