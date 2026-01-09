package entity;

import java.util.UUID;

public class Message {
   private final UUID senderid;
   private final UUID receiverid;
   private final UUID roomid;
   private String content;
   private Long createdAt;
   private Long updatedAt;

   public Message(String content){
       this.senderid = UUID.randomUUID();
       this.receiverid = UUID.randomUUID();
       this.roomid = UUID.randomUUID();
       this.content = content;
       long now= System.currentTimeMillis();
       this.createdAt = now;
       this.updatedAt = now;
   }

    public UUID getSenderid() {
        return senderid;
    }

    public UUID getReceiverid() {
        return receiverid;
    }

    public UUID getRoomid() {
        return roomid;
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
    public void setContent(String content) {
       if(content == null){
           this.content = "";
       }
       else{
           this.content = content;
       }
    }



    public void update(String content){

    }
}
