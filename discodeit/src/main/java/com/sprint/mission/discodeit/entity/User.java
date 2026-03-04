package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@ToString(callSuper = true)
@Getter
public class User extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID profileId;
    private UUID userStateId;
    private String username = "";
    private String email = "";
    private String password = "";

    public User(String userName, String password, String email){
      super();
      this.username = userName;
      this.password = password;
      this.email = email;
      System.out.println("User 생성 - " + this.toString());
    }

    public void updateName(String name){
        this.username = name;
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

    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
        updateUpdateAt();
    }

    public void updateUserStateId(UUID userStateId){
        this.userStateId = userStateId;
    }

    public void update(String newUsername, String newEmail, String newPassword, UUID newProfileId) {
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            anyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }
        if (newProfileId != null && !newProfileId.equals(this.profileId)) {
            this.profileId = newProfileId;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
