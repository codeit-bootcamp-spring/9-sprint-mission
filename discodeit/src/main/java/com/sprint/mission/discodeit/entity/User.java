package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Getter
public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private UUID profileId;
    private UUID userStateId;
    private String name = "";
    private String email = "";
    private String password = "";

    public User(String name, String password, String email){
        super();
        this.name = name;
        this.password = password;
        this.email = email;

        System.out.println("User 생성 - " + this.toString());
    }

    public void updateName(String name){
        this.name = name;
        updateUpdateAt();
    }

    public void updatePassword(String password) {
        this.password = password;
        updateUpdateAt();
    }

    public void updateEmail(String email) {
        this.email = email;
        updateUpdateAt();
    }

    public void updateProfileImageId(UUID profileId){
        this.profileId = profileId;
    }

    public void updateUserStateId(UUID userStateId){
        this.userStateId = userStateId;
    }

    public String toString(){
        String createAtToString = this.createdAt
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return "USER) UUID: " + this.id + " | name: " + this.name + " | phone num: " + this.password + " | e-mail: " + this.email
                + " | Created At: " + createAtToString;
    }

    public void update(String newName, String newEmail, String password) {
        if (newName != null)
            this.name = newName;
        if (newEmail != null)
            this.email = newEmail;
        if (password != null)
            this.password = password;
        updateUpdateAt();
    }
}
