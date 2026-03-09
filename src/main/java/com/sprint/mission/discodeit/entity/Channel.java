package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "channels")
public class Channel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type", nullable = false, columnDefinition = "channel_type")
    private ChannelType channelType;

    @ElementCollection
    @CollectionTable(name = "channel_participants", joinColumns = @JoinColumn(name = "channel_id"))
    @Column(name = "participant_id", columnDefinition = "UUID")
    private List<UUID> participantIds = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
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