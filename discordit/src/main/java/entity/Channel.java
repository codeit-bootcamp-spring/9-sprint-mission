package entity;

import java.util.UUID;

public class Channel {

    public enum ChannelType {
        PUBLIC,
        PRIVATE
    }

    private final UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String channelName;
    private String description;
    private ChannelType channelType;




    //더 낮은 시간대로 내려가면 차이가 있기 않겠냐
    //

    public Channel(ChannelType channelType, String channelName, String description) {
        this.id = UUID.randomUUID();

        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.channelName = channelName;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getDescription() {
        return description;
    }

    public void updateChannel(String channelName, String description){
        this.channelName = channelName;
        this.description = description;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return
                "[Channel정보] \n"+
                        "createdAt   : " + createdAt +"\n"+
                        "updatedAt   : " + updatedAt +"\n"+
                        "C.id        : " + id + "\n"+
                        "channelName : " + channelName + "\n"+
                        "description : " + description + '\n'

                ;
    }
}
