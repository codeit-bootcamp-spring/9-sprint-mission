package com.sprint.mission.discodeit.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String phone;
    private UUID profileImageId;
    private boolean online;
}
