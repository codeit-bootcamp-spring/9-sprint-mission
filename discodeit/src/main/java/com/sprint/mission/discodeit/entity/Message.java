package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {

    private String content;      // 메시지 내용
    private UUID channelId;      // 어떤 채널에 속한 메시지인지
    private UUID authorId;       // 누가 썼는지(User id)

    // 신규 생성용
    public Message(String content, UUID channelId, UUID authorId) {
        super();
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
    }

    // ✅ 파일 복원용 (User처럼 id/time 유지하려면 필요)
    public Message(UUID id, long createdAt, long updatedAt,
                   String content, UUID channelId, UUID authorId) {
        super(id, createdAt, updatedAt);
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    // 수정은 setter 대신 update() 하나로 통일 (User랑 같은 스타일)
    public void update(String content) {
        if (content != null) this.content = content;
        updateTimestamp(); // BaseEntity에 있는 너의 방식 그대로 사용
    }
}

