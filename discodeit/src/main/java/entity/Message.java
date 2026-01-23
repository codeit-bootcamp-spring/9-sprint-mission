package entity;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private String chat;
    private Long createdAt;
    private Long updateAt;
    private String simpleName;
    //private UUID channelId = UUID.randomUUID();
    //private UUID authorId = UUID.randomUUID();

    private String chat() {
        return chat;
    }


    public Message(String chat, UUID channelId, UUID authorId) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
        this.chat = chat;
    }
    public UUID getId() {
        return id;
    }


    public String getchat() {
        return chat;
    }
    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }
    public String getSimpleName() {return simpleName;}

    public void update(String chat) {
        if(chat != null) this.chat = chat;

    }


    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
    }

}

