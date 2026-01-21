package entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private String displayName;
    private String email;
    private String phoneNumber;
    private final Long createdAt;
    private Long updatedAt;

    public User(String displayName, String email, String phoneNumber) {
        this.id = UUID.randomUUID();
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // 정식 Getter (권장)
    public UUID getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    // 정식 Setter/업데이트
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        touch();
    }

    public void setEmail(String email) {
        this.email = email;
        touch();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        touch();
    }

    private void touch() {
        this.updatedAt = System.currentTimeMillis();
    }

    // 인터페이스 updateUser()에 맞춰 사용할 업데이트 메서드
    public void update(String newDisplayName, String newEmail, String newPhoneNumber) {
        this.displayName = newDisplayName;
        this.email = newEmail;
        this.phoneNumber = newPhoneNumber;
        touch();
    }

    // 기존에 작성한 메서드명과의 호환이 필요하면 "브릿지"로 남겨도 됨(선택)
    public UUID getById() { // 기존 코드 호환용
        return id;
    }
    public String getdisplayName() { // 기존 코드 호환용
        return displayName;
    }
    public void setdisplayName(String displayName) { // 기존 코드 호환용
        setDisplayName(displayName);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
