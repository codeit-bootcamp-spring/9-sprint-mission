package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Channel implements Serializable {
    private final UUID id;
    private String name;
    private final ChannelType channelType;
    private final Long  createdAt;
    private Long  updatedAt;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Serial
    private  static final long serialVersionUID = 1L;

    public Channel(String name, ChannelType channelType) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 필수입니다.");
        }

        if (channelType == null) {
            throw new IllegalArgumentException("채널 타입은 필수입니다.");
        }

        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.name = name;
        this.channelType = channelType;
    }

    public UUID getId() { return id;}

    public String getName() {
        return name;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void updateName(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("치널 이름은 비어 있을 수 없습니다.");
        }

        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", channelType=" + channelType +
                ", createdAt=" + sdf.format(new Date(createdAt)) +
                ", updatedAt=" + sdf.format(new Date(updatedAt)) +
                '}';
    }

}
