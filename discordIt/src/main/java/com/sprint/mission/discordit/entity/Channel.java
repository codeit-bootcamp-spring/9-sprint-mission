package entity;

import java.util.UUID;

public class Channel {

    // 필드 선언(어떤 정보들을 가지고 있어야 하는지 정의하는 곳)
    private final UUID id;
    private String name;
    private String description;
    private ChannelType type;
    private final Long createdAt;
    private Long updatedAt;

    // 생성자 (딱 한 번 실행, 필드에 데이터를 채워 넣는 역할)
    public Channel(ChannelType type, String name, String description) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.name = name;
        this.description = description;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // private로 감춰둔 필드를 볼 수 있게 함(캡슐화, getter로 정보 보여주기)
    public UUID getId() {
        return id;
    }

    public ChannelType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    // 정보 수정 메서드 (이름과 설명 변경 및 수정 시간 자동 갱신)
    public void update(String name, String description) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        this.updatedAt = System.currentTimeMillis();
    }
}