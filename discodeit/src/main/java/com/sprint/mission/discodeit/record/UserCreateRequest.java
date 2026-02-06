package com.sprint.mission.discodeit.record;

public record UserCreateRequest (
    String username,
    String email,
    String password,
    String url
){}
