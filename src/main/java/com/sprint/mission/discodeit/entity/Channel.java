package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private String name;
    private String description;
    private final ChannelType channelType;
    private final List<UUID> participantIds;
    private final Instant createdAt;
    private Instant updatedAt;

    public Channel(String name, String description, ChannelType type) {
        if (type == null || type != ChannelType.PUBLIC) {
            throw new IllegalArgumentException("PUBLIC 채널 생성 시 channelType은 PUBLIC이어야 합니다.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 필수입니다.");
        }
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.channelType = type;
        this.participantIds = new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public Channel(List<UUID> participantUserIds) {
        if (participantUserIds == null || participantUserIds.isEmpty()) {
            throw new IllegalArgumentException("PRIVATE 채널 참여자는 최소 1명 이상이어야 합니다.");
        }
        this.id = UUID.randomUUID();
        this.name = null;
        this.description = null;
        this.channelType = ChannelType.PRIVATE;
        this.participantIds = new ArrayList<>(participantUserIds);
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void updateName(String name) {
        if (channelType == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 비어 있을 수 없습니다.");
        }
        this.name = name;
        this.updatedAt = Instant.now();
    }

    public void updateDescription(String description) {
        if (channelType == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public List<UUID> getParticipantIds() {
        return Collections.unmodifiableList(participantIds);
    }

    public boolean isParticipant(UUID userId) {
        return participantIds.contains(userId);
    }

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", channelType=" + channelType +
                ", createdAt=" + (createdAt != null ? FORMATTER.format(createdAt) : null) +
                ", updatedAt=" + (updatedAt != null ? FORMATTER.format(updatedAt) : null) +
                '}';
    }
}
