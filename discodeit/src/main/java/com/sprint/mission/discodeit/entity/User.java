package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

//  BinaryContent를 필드로 두지 말고 UUID로만 참조 하도록 설계
    private UUID profileImageId;
    private UUID binaryContentLd;
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private String username;
    private String email;
    private String password;

    public User(String username, String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void update(UserCreateRequestDto requestDto) {
        if (requestDto.username() != null) this.username = requestDto.username();
        if (requestDto.email() != null) this.email = requestDto.email();
        if (requestDto.password() != null) this.password = requestDto.password();
        if (requestDto.profileImage() != null)
            this.profileImageId = requestDto.profileImage().id();
        this.updatedAt = Instant.now();
    }
}