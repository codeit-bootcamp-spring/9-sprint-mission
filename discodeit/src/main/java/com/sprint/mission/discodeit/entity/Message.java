package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
public class Message {

    @Id
    private UUID id;

    private Instant createdAt;
    private Instant updatedAt;

    private String content;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany
    @JoinTable(
        name = "message_attachments",
        joinColumns = @JoinColumn(name = "message_id"),
        inverseJoinColumns = @JoinColumn(name = "binary_content_id")
    )
    private List<BinaryContent> attachments;

    protected Message() {}

    public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.content = content;
        this.channel = channel;
        this.author = author;
        this.attachments = attachments;
    }

    public void update(String newContent) {
        this.content = newContent;
        this.updatedAt = Instant.now();
    }
}
