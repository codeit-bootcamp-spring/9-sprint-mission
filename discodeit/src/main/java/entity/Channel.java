package entity;

import java.util.UUID;

public class Channel {
    private final UUID id;
    private final String channelName;
    private final Long createdAt;
    private final Long updateAt;

    public Channel(String channelName) {
        this.id = UUID.randomUUID();
        this.channelName = channelName;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updateAt = now;
    }

    public UUID getId() {
        return id;
    }

    public String getChannelName() {
        return channelName;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }
}