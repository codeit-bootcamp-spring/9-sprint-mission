package com.sprint.mission.discodeit.DTO;

import com.sprint.mission.discodeit.entity.User;

public class LoginDto {
    public record LoginUser(
            String username,
            String password,
            String email
    ){}
}
