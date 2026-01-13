package entity;

import java.util.UUID;

public class Channel {
    private final UUID id;
    private String channelName;
    private Long createdAt;
    private Long updateAt;
    private String frame;
    private String detail;

    public Channel(String frame, String channelName, String detail) {
        this.id = UUID.randomUUID();
        this.channelName = channelName;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updateAt = now;
        this.frame = frame;
        this.detail = detail;
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

    public String getFrame() {
        return frame;
    }

    public String getDetail() {
        return detail;
    }


    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", channelName='" + channelName + '\'' +
                ", createdAt=" + createdAt +
                ", updateAt=" + updateAt +
                ", frame=" + frame +
                ", detail=" + detail +
                '}';
    }


}