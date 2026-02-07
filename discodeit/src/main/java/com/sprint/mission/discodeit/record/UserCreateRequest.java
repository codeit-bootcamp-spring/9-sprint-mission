package com.sprint.mission.discodeit.record;

public record UserCreateRequest(
        String userName,
        String email,
        String password,
        String profileUrl
) {}
