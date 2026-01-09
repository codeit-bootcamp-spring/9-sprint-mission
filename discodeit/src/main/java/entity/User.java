package entity;

import java.util.UUID;

public class User {
    private UUID id;
    private Long createdAt;
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

    public Long getCreateAt() {
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
        return "User{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    public void update(String username, String email, String phoneNumber) {
            this.username = username;
            this.email = email;
            this.phoneNumber = phoneNumber;
    }

}