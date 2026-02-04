package entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String username;
    private String email;
    private String phoneNumber;

    public User(String username, String email, String phoneNumber) {
        this.id = UUID.randomUUID();
        long now = System.currentTimeMillis();
        this.updatedAt = now;
        this.createdAt = now;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String toString() {
        return String.format("아이디:%s/생성시간:%s/업데이트 시간:%s/\n이름: %s / 이메일: %s / 전화번호: %s",getId(),getCreatedAt(),getUpdatedAt(),getUsername(), getEmail(), getPhoneNumber());
    }
    public void update(String username, String email, String phoneNumber) {
            this.username = username;
            this.email = email;
            this.phoneNumber = phoneNumber;
            long now=System.currentTimeMillis();
            this.updatedAt = now;
    }

}