package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@ToString(callSuper = true)
@Getter
public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private UUID profileId;
    private UUID userStateId;
    private String userName = "";
    private String email = "";
    private String password = "";

    public User(String userName, String password, String email){
        super();
        this.userName = userName;
        this.password = password;
        this.email = email;

        System.out.println("User 생성 - " + this.toString());
    }

    public void updateName(String name){
        this.userName = name;
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

    public void update(String newName, String newEmail, String password) {
        if (newName != null)
            this.userName = newName;
        if (newEmail != null)
            this.email = newEmail;
        if (password != null)
            this.password = password;
        updateUpdateAt();
    }
}
