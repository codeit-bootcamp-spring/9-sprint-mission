package entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    private String newMessage;
    private UUID channelId;
    private UUID authorId;

    public Message(String newMessage, UUID channelId, UUID authorId) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.newMessage = newMessage;
        this.channelId = channelId;
        this.authorId = authorId;
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

    public String getMessage() {
        return newMessage;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public  void getAllMessage(){ }

    public void updateMessage(UUID id, String newMessage){
        this.newMessage = newMessage;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return
                "[Message정보] \n"+
                        "createdAt   : " + createdAt +"\n"+
                        "updatedAt   : " + updatedAt +"\n"+
                        "M.id        : " + id + "\n"+
                        "message     : " + newMessage + "\n"
                ;
    }

}
