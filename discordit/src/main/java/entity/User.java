package entity;

import java.util.UUID;

public class User {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    //추가적으로 필요한 필드들 추가. 예시) 유저네임,
    private String displayName;
    private String email;
    private String phoneNumber;

    //생성자 메소드
    public User(String displayName, String email, String phoneNumber) {
        this.id = UUID.randomUUID();
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
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

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void getAllUser() {
    }

    //TODO 업데이트 추가

    public void update(String displayName, String email, String phoneNumber){
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return
                "--------------- <<User정보 >> ---------------\n"+
                "createdAt   : " + createdAt +"\n"+
                "updatedAt   : " + updatedAt +"\n"+
                "id          : " + id + "\n"+
                "name        : " + displayName + "\n"+
                "email       : " + email + '\n' +
                "phoneNumber : " + phoneNumber + '\n'
                ;
    }
}
