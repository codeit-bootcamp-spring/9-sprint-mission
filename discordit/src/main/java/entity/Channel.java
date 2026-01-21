package entity;

import java.io.Serializable;
import java.util.UUID;

public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID totalId;
    private String ownerId;
    private String channelName;
    private String description;
    private Long createdAt;
    private Long updatedAt;

    public Channel(String channelName, String description, String ownerId) {
        this.totalId = UUID.randomUUID();
        this.channelName = channelName;
        this.description = description;
        this.ownerId = ownerId;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public String getDescription() {
        return description;
    }

    public UUID getId() {
        return totalId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }


    public String getownerId() {
        return ownerId;
    }
}
