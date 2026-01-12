package entity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Message {
    private final UUID id = UUID.randomUUID();
    private final long createdAt;
    private long updatedAt;

    // 이름이 아닌 UUID 저장
    private final User writer;
    private final Channel channel;
    private String content = "";

    public Message(User writer, Channel channel, String content){
        this.writer  = writer;
        this.channel = channel;
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

    public User getWriter () {
        return writer ;
    }

    public Channel getChannel() {return channel;}

    public void updateUpdateAt(){
        this.updatedAt = System.currentTimeMillis();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void PrintInfo(){
        String createAtToString = Instant.ofEpochMilli(this.createdAt)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("MESSAGE) UUID: " + this.id + " | User: " + this.writer.getName() + " | Channel: " + this.channel.getName() +
                "\n Content: " + this.content
                + "\n Created At: " + createAtToString);
    }
}
