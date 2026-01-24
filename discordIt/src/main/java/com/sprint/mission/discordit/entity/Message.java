package entity;

import java.util.UUID;

public class Message {

    // 필드 선언
    private final UUID id;
    private String content;
    private final UUID channelId;
    private final UUID authorId;
    private final Long createdAt;
    private Long updatedAt;

    // 생성자 (선언된 필드에 처음으로 정보를 넣어주며, 딱 한 번만 실행, Constructor)
    public Message(String content, UUID channelId, UUID authorId) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // private로 감춰진 정보를 볼 수 있게 getter, 캡슐화
    public UUID getId() {
        return id;
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

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    // 정보를 수정하기 위한 메서드 (update 인자로 content가 들어가 있는 이유는 외부에서 들어오는 정보이기 때문에)
    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }
}