package entity;

public class User extends BaseUser {

    private String userId;
    private String email;
    private String name;
    private String userNumber;

    public User(String userId, String email, String name, String userNumber) {
        super();
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.userNumber = userNumber;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getUserNumber() {
        return userNumber;

    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", userNumber='" + userNumber + '\'' +
                '}';
    }
}



