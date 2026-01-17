package entity;

public class User extends BaseEntity {
    private String loginId;
    private String password;
    private String username; //실명
    private String nickname; //닉네임 *선택사항
    private String phoneNumber;

    public User(String loginId, String password, String username, String phoneNumber, String nickname) {
        super();
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.phoneNumber = phoneNumber;

        if (nickname != null) {
            this.nickname = nickname;
        } else {
            this.nickname = username;
        }
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
