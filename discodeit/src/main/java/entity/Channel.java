package entity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Channel {
    private final UUID id;
    private ChannelType type;
    private final long createdAt;
    private long updatedAt;

    private String name = "";

    private final Set<UUID> members = new HashSet<>();
    private final Set<UUID> messages = new HashSet<>();

    public Channel(ChannelType type, String name){
        this.id = UUID.randomUUID();
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

    public void AddMember(UUID userId){
        boolean ret = members.add(userId);
        if (ret == true) {
            updateUpdateAt();
        }
        else{
            throw new IllegalStateException("채널에 유저 추가 실패 | 채널ID: " + this.id + " | 유저ID: " + userId);
        }
    }

    public void RemoveMember(UUID userId){
        boolean ret = members.remove(userId);
        if (ret == true){
            updateUpdateAt();
        }
        else {
            throw new IllegalStateException("채널에서 유저 삭제 실패 | 채널ID: " + this.id + " | 유저ID: " + userId);
        }
    }

    public void AddMessage(UUID messageId){
        boolean ret = messages.add(messageId);
        if (ret == true){
            updateUpdateAt();
        }
        else {
            throw new IllegalStateException("채널에 메시지 추가 실패 | 채널ID: " + this.id + " | 유저ID: " + messageId);
        }
    }

    public void RemoveMessage(UUID messageId){
        boolean ret = messages.remove(messageId);
        if (ret == true){
            updateUpdateAt();
        }
        else {
            throw new IllegalStateException("채널에서 메시지 삭제 실패 | 채널ID: " + this.id + " | 유저ID: " + messageId);
        }
    }

    public void PrintInfo(){
        String createAtToString = Instant.ofEpochMilli(this.createdAt)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("Channel) UUID: " + this.id + " | name: " + this.name + " | Created At: " + createAtToString);
    }
}
