package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

/* User객체에 들어갈 정보를 private변수 선언을 한다
public User 입력값(매개변수)을 입력한다
유저객체의 변수에 입력변수(매개변수)의 값을 입력한다
UUID부터 메서드들은 리턴값을 반환한다
 */

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String displayName;
    private String email;
    private String phoneNumber;
    private Long createdAt;
    private Long updatedAt;

    public User(String displayName, String email, String phoneNumber) {
        this.id = UUID.randomUUID();
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }
    //
    public void update(String displayName, String email, String phoneNumber) {
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
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

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String toString() {
        return "User [이름=" + displayName + ", 이메일=" + email + ", 전화번호=" + phoneNumber + "]";
    }

    //TODO 메서드 추가
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}