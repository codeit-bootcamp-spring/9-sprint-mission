package main.java.com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class Channel implements Serializable {
    private final UUID id;
    private String channelName;
    private String description;
    private final Long createdAt;
    private Long updatedAt;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Long now = System.currentTimeMillis();

    public Channel (String channelName, String description) {
        this.id = UUID.randomUUID();
        this.channelName = channelName;
        this.description = description;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public UUID getId() {
        return id;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getDescription() {
        return description;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void update(UUID id, String channelName, String description) {
        this.channelName = channelName;
        this.description = description;
        this.updatedAt = now;
    }

    @Override
    public String toString() {
        return "Channel{" +
                ", channelName='" + channelName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
