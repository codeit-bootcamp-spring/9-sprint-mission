package entity;

import java.util.UUID;

public class User {

    // 필드 선언
    private final UUID id;
    private String username;
    private String email;
    private String password;   // [추가] 비밀번호 필드
    private final Long createdAt;
    private Long updatedAt;

    // 생성자, 필드에 정보를 넣어주며 딱 한 번만 실행
    public User(String username, String email, String password) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.password = password;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // getter, private로 감춰진 정보를 볼 수 있게 함
    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    // 인자 값으로 외부에서 오는 정보인 username, email, password를 넣음, 정보 수정을 위한 메서드
    public void update(String username, String email, String password) {
        if (username != null) this.username = username;
        if (email != null) this.email = email; //
        if (password != null) this.password = password;
        this.updatedAt = System.currentTimeMillis();
    }
}