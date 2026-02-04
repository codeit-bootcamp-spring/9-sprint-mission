package entity;

import java.util.UUID;

public class ChannelMessage {
    private Long id;
    private User sender;
    private User receiver;
    private String content;
    private Long createdAt;
    private Long updatedAt;
    private Channel channel;

    public ChannelMessage(String content,User sender,Channel channel){
        this.id= UUID.randomUUID().getLeastSignificantBits();
        this.content=content;
        this.sender=sender;
        Long now= System.currentTimeMillis();
        this.createdAt=now;
        this.updatedAt=now;
        this.channel=channel;

    }
    public ChannelMessage(User receiver,String content,Channel channel){
        this.id= UUID.randomUUID().getLeastSignificantBits();
        this.content=content;
        this.receiver=receiver;
        Long now= System.currentTimeMillis();
        this.createdAt=now;
        this.updatedAt=now;
    }

    public Long getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public Channel getChannel() {
        return channel;
    }


    public String toString(){
        return String.format("");
    }
    public void update(String content,User sender,User receiver){
        this.content=content;
        this.sender=sender;
        this.receiver=receiver;
        Long now= System.currentTimeMillis();
        this.updatedAt=now;
    }
}
