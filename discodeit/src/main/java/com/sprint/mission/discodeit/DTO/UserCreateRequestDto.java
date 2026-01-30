package com.sprint.mission.discodeit.DTO;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.UserService;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCreateRequestDto {

    private final String username;
    private final String email;
    private final String password;
    private final BinaryContentDto profileImage;

}
