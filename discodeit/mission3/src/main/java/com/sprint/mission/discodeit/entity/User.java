package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.DTO.MyUserDto;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;


@Getter

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID profileId;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private String username;
    private String email;
    private String password;

    public User(MyUserDto.BasicInfo dto) {
        this.id = UUID.randomUUID();
        this.profileId = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt=null;
        this.username = dto.username();
        this.email = dto.email();
        this.password = dto.password();
    }




    public void update(String newUsername, String newEmail, String newPassword) {
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

        if (anyValueUpdated) {
           this.updatedAt=Instant.now();
        }
    }
}
