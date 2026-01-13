package entity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {
    private final UUID id;
    private ChannelType type;
    private final long createdAt;
    private long updatedAt;

    private String name = "";

    private final List<UUID> members = new ArrayList<>();
    private final List<UUID> messages = new ArrayList<>();

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

    public boolean AddMember(UUID userId){
        boolean ret = members.add(userId);
        if (ret == true) {
            updateUpdateAt();
        }
        else{

        }
        return ret;
    }

    public boolean RemoveMember(UUID userId){
        boolean ret = members.remove(userId);
        if (ret == true){
            updateUpdateAt();
        }
        else {
        }
        return ret;
    }

    public boolean AddMessage(UUID messageId){
        boolean ret = messages.add(messageId);
        if (ret == true){
            updateUpdateAt();
        }
        else {
        }
        return ret;
    }

    public boolean RemoveMessage(UUID messageId){
        boolean ret = messages.remove(messageId);
        if (ret == true){
            updateUpdateAt();
        }
        else {
        }
        return ret;
    }

    public void PrintInfo(){
        String createAtToString = Instant.ofEpochMilli(this.createdAt)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("Channel) UUID: " + this.id + " | name: " + this.name + " | Created At: " + createAtToString);
    }
}
