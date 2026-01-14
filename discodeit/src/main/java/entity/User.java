package entity;


import java.util.UUID;

public class User {
    private final UUID id;
    private String displayName;
    private String email;
    private String phoneNumber;
    private final Long createdAt;
    private Long updatedAt;

    //생성자 생성
    public User(String displayName, String email, String phoneNumber) {
        this.id = UUID.randomUUID();
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        long now = System.currentTimeMillis(); //자릿수가 큰 녀석들
        this.createdAt = now;
        this.updatedAt = now;
    }

    public UUID getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    // 아직은 생성과 업데이트를 받아올 서버가 없어서 비활성화인건감
    public final Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    // 도메인이 알 수 있는 것은 본인 정보와 업데이트 여부
    public void update(String displayName, String email, String phoneNumber) {
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.updatedAt = System.currentTimeMillis();
    }
}

