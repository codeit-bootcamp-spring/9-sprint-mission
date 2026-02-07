package com.sprint.mission.discodeit.dto.user;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class UserLoginResponse {
    private UUID id;
    private String username;
    private String email;
    private boolean online;
}