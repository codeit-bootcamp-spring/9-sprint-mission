package com.sprint.mission.discodeit.dto.user;

public record UserCreateRequest(
        UserParams user,
        ProfileImageParams profileImage
) {

}