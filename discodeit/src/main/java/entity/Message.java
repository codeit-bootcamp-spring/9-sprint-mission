package entity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Message {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;

    private final UUID writer;
    private final UUID channel;
    private String content = "";

    public Message(UUID writerId, UUID channelId, String content){
        this.id = UUID.randomUUID();
        this.writer  = writerId;
        this.channel = channelId;
        this.content = content;
        this.createdAt = this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public UUID getWriter () {
        return writer ;
    }

    public UUID getChannel() {return channel;}

    public void updateUpdateAt(){
        this.updatedAt = System.currentTimeMillis();
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String content) {
        this.content = content;
        updateUpdateAt();
    }

    public void PrintInfo(User writer, Channel channel){
//        String createAtToString = Instant.ofEpochMilli(this.createdAt)
//                .atZone(ZoneId.systemDefault())
//                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("MESSAGE) UUID: " + this.id + " | User: " + writer.getName()
                + " | Channel: " + channel.getName() + " | Content: " + this.content);
    }
}
