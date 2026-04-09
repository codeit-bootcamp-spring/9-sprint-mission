package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {

    private String content;
    private final UUID channelId;
    private final UUID authorId;
    private final List<UUID> attachmentIds;

    public Message(String content, UUID channelId, UUID authorId) {
        super();
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = new ArrayList<>();
    }

    // 파일 저장소 복원용 (※ FileMessageRepository가 이 생성자 써야 함)
    public Message(UUID id, Instant createdAt, Instant updatedAt,
                   String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        super(id, createdAt, updatedAt);
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = (attachmentIds == null) ? new ArrayList<>() : new ArrayList<>(attachmentIds);
    }

    // 수정은 setter 대신 update() 하나로 통일 (User랑 같은 스타일)
    public void update(String newContent) {
        this.content = newContent;
        touch();
    }

    public void addAttachment(UUID binaryContentId) {
        this.attachmentIds.add(binaryContentId);
        touch();
    }
}

