package entity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {
    private final UUID id = UUID.randomUUID();
    private ChannelType type;
    private final long createdAt;
    private long updatedAt;

    private String name = "";

    private List<User> members = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

    public Channel(ChannelType type, String name){
        this.type = type;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void updateUpdateAt(){
        this.updatedAt = System.currentTimeMillis();
    }

    public void UpdateName(String name){
        this.name = name;
    }

    public void AddMember(User user){
        members.add(user);
        updateUpdateAt();
    }

    public void RemoveMember(User user){
        boolean ret = members.remove(user);

        if (ret == false){
            // 실패 시 코드
        }
        else {
            // 성공 시 코드
        }
    }

    public void AddMessage(Message message){
        messages.add(message);
        updateUpdateAt();
    }

    public void RemoveMessage(Message message){
        boolean ret = messages.remove(message);

        if (ret == false){
            // 실패 시 코드
        }
        else {
            // 성공 시 코드
        }
    }

    public void PrintInfo(){
        String createAtToString = Instant.ofEpochMilli(this.createdAt)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("Channel) UUID: " + this.id + " | name: " + this.name + " | Created At: " + createAtToString);
    }
}
