package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "Auth API")
public interface AuthApi {

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인합니다."
    )
    UserDto login(@RequestBody LoginRequest request);
}