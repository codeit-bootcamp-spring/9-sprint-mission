package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserUpdateRequest {
    private UUID id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private BinaryContentCreateRequest profileImage;
}
