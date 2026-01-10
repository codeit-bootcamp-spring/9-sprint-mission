package entity;

public class User extends BaseUser {

    private String email;
    private String userName;
    private String number;

    // 생성자
    public User(String email, String userName, String number) {
        // id, createdAt, updatedAt 초기화
        super();
        this.email = email;
        this.userName = userName;
        this.number = number;
    }

    // getters
    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public String getNumber() {
        return number;
    }

    // update 메서드
    public void update(String email, String userName, String number) {
        this.email = email;
        this.userName = userName;
        this.number = number;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", number='" + number + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
