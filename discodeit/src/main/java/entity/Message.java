package entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private String content;
    private String chat;
    private Long createdAt;
    private Long updateAt;



    private String content() {return content;}
    private String chat() {return chat;
    }


    public Message(String chat) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
        this.chat = chat();
        this.content = content();
    }



    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public String getChat() {
        return chat;
    }

    public String getContent() {return content;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
    }

    public void setContent(String content) {
        this.content = content;
    }
}





