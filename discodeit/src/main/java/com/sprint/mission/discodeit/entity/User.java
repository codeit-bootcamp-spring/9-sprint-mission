package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {
    private String email;
    private String nickname;
    private String password;
    private UserStatus status;

    //생성자
    public User (String email, String nickname, String password){
        if(email.isBlank()){
            throw new IllegalArgumentException("이메일을 비워둘 수 없습니다.");
        }else if(nickname.isBlank()){
            throw new IllegalArgumentException("닉네임을 비워둘 수 없습니다.");
        }else if(password.isBlank()){
            throw new IllegalArgumentException("비밀번호를 비워둘 수 없습니다.");
        }
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.status = UserStatus.ONLINE;
    }

    //닉네임 변경
    public void updateNickname(String nickname){
        if(nickname != null && !nickname.isEmpty()){
            this.nickname = nickname;
            this.setUpdatedAt(System.currentTimeMillis());
        }else{
            throw new IllegalArgumentException("닉네임은 비워둘 수 없습니다.");
        }
    }

    public void updatePassword(String password){
        if(password != null && !password.isEmpty()){
            this.password = password;
            this.setUpdatedAt(System.currentTimeMillis());
        }else{
            throw new IllegalArgumentException("비밀번호는 비워둘 수 없습니다.");
        }
    }

    public void setStatus(UserStatus status){
        if(status == null){
            throw new IllegalArgumentException("상태값을 제대로 입력해주세요.");
        }
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public UserStatus getStatus() {
        return status;
    }

}
