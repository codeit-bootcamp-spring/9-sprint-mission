package entity;

import java.util.UUID;

public class User {
    private UUID id;
    private String userName;
    private String email;
    private String phoneNumber;
    private Long createdAt;
    private Long updateAt;

    public User(String userName, String email, String phoneNumber) {
        this.id = UUID.randomUUID();
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updateAt = now;
    }

    public UUID getId() { return id; }

    public String getUserName(){ return userName;}

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdateAt() {return updateAt;}


    // 선택적 업데이트를 위한 수정용 Setter
    public void update(String userName, String email, String phoneNumber) {
        if (userName != null) this.userName = userName;
        if (userName != null) this.email = email;
        if (userName != null) this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", createdAt=" + createdAt +
                ", updateAt=" + updateAt +
                '}';
    }


    //오버라이딩
    //제네이이트 -> 'toString'
}
