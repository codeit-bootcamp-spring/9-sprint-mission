package com.sprint.mission.discodeit.dto.data;


import com.sprint.mission.discodeit.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record UserDto(
    @Schema(description = "유저 아이디")
    UUID id,
    @Schema(description = "유저 이름")
    String username,
    @Schema(description = "유저 이메일")
    String email,
    @Schema(description = "프로필 파일")
    BinaryContentDto profile,
    @Schema(description = "유저 온라인 여부")
    Boolean online,
    Role role
) {

}
