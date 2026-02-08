package com.sprint.mission.discodeit.entity;


import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;


@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String userName;
    private String email;
    private String password;

    private final UUID profileId;
    private String profileImage;
    private boolean online;
    private long lastOnline;

    public User(String userName, String email, String password, String profileImage) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().toEpochMilli();
        this.updatedAt = this.createdAt;
        this.userName = userName;
        this.email = email;
        this.password = password;

        this.profileId = UUID.randomUUID();
        this.profileImage = profileImage;

    }


    public UUID getUserId() {
        return this.id;
    }

//    public LocalDateTime getCreatedAtLocalDateTime(){
//        return LocalDateTime.ofInstant(Instant.ofEpochMilli(this.createdAt)
//                , java.time.ZoneId.systemDefault());
//    }
//
//    public LocalDateTime getUpdatedAtLocalDateTime() {
//        return LocalDateTime.ofInstant(Instant.ofEpochMilli(this.updatedAt)
//        , java.time.ZoneId.systemDefault());
//    }


    public void updateUserName(String userName){
        this.userName = userName;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateEmail(String email){
        this.email = email;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updatePassword(String password){
        this.password = password;
        this.updatedAt = System.currentTimeMillis();
    }



    @Override
    public String toString() {
        return "User{" +
                "id=" + this.id +
                ", username='" + this.userName + '\'' +
                ", email='" + this.email + '\'' +
                ", password='" + this.password + '\'' +
                '}';
    }




}
