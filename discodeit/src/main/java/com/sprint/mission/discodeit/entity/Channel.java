package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID channelId;
    private String name;
    private String displayname;
    private UUID admin;
    private List<UUID> members;

    public Channel(String name, String displayname, UUID admin) {
        this.channelId = UUID.randomUUID();
        this.name = name;
        this.displayname = displayname;
        this.admin = admin;
        this.members = new ArrayList<>();
        this.members.add(admin); // 관리자는 자동으로 멤버로 투입 (관리자지만 사용 가능하도록 설정)
    }

    public UUID getId() {
        return channelId;
    }

    public UUID getAdmin() {
        return admin;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void changeDisplayName(String newDisplayName) {
        this.displayname = newDisplayName;
    }

    @Override
    public String toString() {
        return "Channel: " +
                "name='" + name + '\'' +
                ", displayname='" + displayname + '\'' +
                ']';
    }
}

