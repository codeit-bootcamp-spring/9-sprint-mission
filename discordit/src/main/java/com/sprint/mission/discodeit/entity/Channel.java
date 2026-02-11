package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createAt;
    private final String createUser;
    private final Map<String, UUID> accessibleUser = new ConcurrentHashMap<>();
    private final Map<String, ReadStatus> userReadStatusMap = new ConcurrentHashMap<>();
    private final ChannelType channelType;
    private String name;
    private Instant updateAt;

    public Channel(String name, String createUser, UUID createUserId) {
        this(name, createUser, createUserId, ChannelType.PUBLIC);
    }

    public Channel(String name, String createUser, UUID createUserId, ChannelType channelType) {
        Instant now = Instant.now();
        this.id = UUID.randomUUID();
        this.name = name;
        this.createUser = createUser;
        this.channelType = channelType;
        this.createAt = now;
        this.updateAt = now;
        accessibleUser.put(createUser, createUserId);
    }

    /// setter
    private void setUpdateAt() {
        this.updateAt = Instant.now();
    }

    private void setName(String name) {
        this.name = name;
        setUpdateAt();
    }

    public void channelUpdater(String name) {
        setName(name);
    }

    public void addAccessibleUser(String userName, UUID userId) {
        accessibleUser.put(userName, userId);
    }

    public void removeAccessibleUser(String userName) {
        accessibleUser.remove(userName);
    }
}