package entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Channel implements Serializable {

    private static final long serialVersionUID = 1L;
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

    public void updateName(String name){
        this.name = name;
    }

    public boolean addMember(UUID userId){
        boolean ret = members.add(userId);
        if (ret) {
            updateUpdateAt();
        }
        return ret;
    }

    public boolean removeMember(UUID userId){
        boolean ret = members.remove(userId);
        if (ret){
            updateUpdateAt();
        }
        return ret;
    }

    public boolean addMessage(UUID messageId){
        boolean ret = messages.add(messageId);
        if (ret){
            updateUpdateAt();
        }

        return ret;
    }

    public boolean removeMessage(UUID messageId){
        boolean ret = messages.remove(messageId);
        if (ret){
            updateUpdateAt();
        }
        return ret;
    }

    public String toString(){
        String createAtToString = Instant.ofEpochMilli(this.createdAt)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return "Channel) UUID: " + this.id + " | name: " + this.name + " | Created At: " + createAtToString;
    }
}
