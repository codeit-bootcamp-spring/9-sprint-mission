package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.type.ChannelType;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

@ToString(callSuper = true)
@Getter
public class Channel extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final ChannelType type;
    private String name = "";
    private String description = "";

    private final Set<UUID> members = new HashSet<>();
    private final List<UUID> messages = new ArrayList<>();

    public Channel(ChannelType type, String name, String description){
        super();
        this.type = type;
        this.name = name;
        this.description = description;

        System.out.println("Channel 생성 - " + this.toString());
    }

    public void update(String name, String description){
        if (name != null) {
            this.name = name;
        }
        if (description != null){
            this.description = description;
        }
        updateUpdateAt();
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

    public List<UUID> getMemberList(){
        return members.stream().toList();
    }

    public List<UUID> getMessageList(){
        return messages;
    }

}
