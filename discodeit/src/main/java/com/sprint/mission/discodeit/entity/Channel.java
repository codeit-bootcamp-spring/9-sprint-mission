package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.type.ChannelType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;

@Entity
@Table(name = "channels")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Channel extends BaseUpdatableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType type;

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
    }

    public boolean addMember(UUID userId){
        boolean ret = members.add(userId);
        return ret;
    }

    public boolean removeMember(UUID userId){
        boolean ret = members.remove(userId);
        return ret;
    }

    public boolean addMessage(UUID messageId){
        boolean ret = messages.add(messageId);

        return ret;
    }

    public boolean removeMessage(UUID messageId){
        boolean ret = messages.remove(messageId);
        return ret;
    }

    public List<UUID> getMemberList(){
        return members.stream().toList();
    }

    public List<UUID> getMessageList(){
        return messages;
    }


}
