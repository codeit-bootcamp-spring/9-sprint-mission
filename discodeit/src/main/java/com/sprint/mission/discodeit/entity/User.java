package com.sprint.mission.discodeit.entity;

import java.util.UUID;
// 변수 선언
public class User {
    private final UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private final Long createdAt;
    private Long updatedAt;

    //  생성자 생성
    public User(String name, String email, String phoneNumber) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        Long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // getter 생성
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public User update(String name, String email, String phoneNumber) {
        // null 값이 아닌 경우 매개 변수로 가져와 수정 후 리턴 (생성자 없음)
        if (name != null) this.name = name;
        if (email != null) this.email = email;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;

        return this;
    }

    @Override
    public String toString() {
        // 다건 조회 병렬로 출력
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}


