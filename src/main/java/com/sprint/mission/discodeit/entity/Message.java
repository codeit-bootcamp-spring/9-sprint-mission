package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToMany
    @JoinTable(
        name = "message_attachments",
        joinColumns = @JoinColumn(name = "message_id"),
        inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContent> attachments = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Message(Channel channel, User author, String content) {
        if (channel == null) {
            throw new IllegalArgumentException("채널은 필수입니다.");
        }

        this.channel = channel;
        this.author = author;
        this.content = content != null ? content : ""; // null이면 빈 문자열로
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void updateContent(String newContent) {
        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }

        this.content = newContent;
        this.updatedAt = Instant.now();
    }

    public UUID getChannelId() {
        return channel != null ? channel.getId() : null;
    }

    public UUID getSenderId() {
        return author != null ? author.getId() : null;
    }

    public void addAttachment(BinaryContent attachment) {
        if (attachment != null) {
            this.attachments.add(attachment);
        }
    }

    public List<UUID> getAttachmentIds() {
        return attachments.stream()
            .map(BinaryContent::getId)
            .toList();
    }
}