package entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;


    private final UUID id;
    private String userName;
    private String email;
    private String phoneNumber;
    private Long createdAt;
    private Long updateAt;
    private String simpleName;
    //Long now = System.currentTimeMillis();

    public User(String userName, String email, String phoneNumber) {
        this.id = UUID.randomUUID();
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updateAt = now;
        this.simpleName = simpleName;
    }

    public UUID getId() { return id; }

    public String getuserName(){ return userName;}

    public String getemail() {
        return email;
    }

    public String getphoneNumber() {
        return phoneNumber;
    }

    public String getSimpleName() {return simpleName; }

    // 생성 시간이 필요한가?

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdateAt() {return updateAt;}

    //용어 정리가 안됨 대소문자 구분
    /*public void setuserName(String userName) {
        this.userName = userName;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public void setphoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
*/
    // 선택적 업데이트를 위한 수정용 Setter
    public void update(String userName, String email, String phoneNumber) {
        if (userName != null) this.userName = userName;
        if (email != null) this.email = email;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
    }


    // toString 다시 공부
    @Override
    public String toString() {
        return "User{" +
                "id=" + id + '\'' + ", userName='" + userName + '\'' + ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", createdAt=" + createdAt +
                ", updateAt=" + updateAt +
                '}';
    } //호출한거에 값을 각 객체에 반환하도록



    //오버라이딩
    //제네이이트 -> 'toString'
}
