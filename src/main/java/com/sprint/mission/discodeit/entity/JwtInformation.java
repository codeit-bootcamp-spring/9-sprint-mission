package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.data.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JwtInformation {
    private UserDto userDto;
    String accessToken;
    String refreshToken;


    public void rotate(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public boolean isExpired() {
        return false;
    }
}
