package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel implements Serializable {    //객체 직렬화
    private static final long serialVersionUID = 1L;
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private ChannelType type;
    private String name;
    private String description;

    public Channel(ChannelType type, String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.type = type;
        this.name = name;
        this.description = description;
    }
    private List<UUID> members = new ArrayList<>();

    public boolean containsUser(UUID userId) {
        return this.members.contains(userId);
    }

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }
        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
/* 메소드의 매개변수를 받아와서 실제로 데이터가 바꼈는가
if1 새 이름이 비어있지 않고 null이 아님, 현재 이름과 다를때만 수정한다.
조건이 맞으면 이름을 새로 바꾸고 데이터가 변경되었으므로 true로 바꿈 채널 설명도 위랑 마찬가지로 다를때 수정
즉 이름이나 설명중 하나라도 실제로 바뀌었으면 true, updatedat 필드에 현재 시간을 기록
 */