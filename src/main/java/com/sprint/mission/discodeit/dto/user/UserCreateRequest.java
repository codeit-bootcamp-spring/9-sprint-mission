package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {
    private String username;
    private String email;
    private String phone;
    private String password;
    private BinaryContentCreateRequest profileImage;
}
