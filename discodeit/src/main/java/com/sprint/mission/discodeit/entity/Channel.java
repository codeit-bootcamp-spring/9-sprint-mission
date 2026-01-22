package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity {
    private String channelName;
    private String description;

    public Channel(String name, String description){
        if(name.isBlank() || name == null){
            throw new IllegalArgumentException("채널 이름을 비워둘 수 없습니다.");
        }
        this.channelName = name;
        this.description = description;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getDescription() {
        return description;
    }

    public void update(String name,String description){
        this.channelName = name;
        this.description = description;
        this.setUpdatedAt(System.currentTimeMillis());
    }
}
