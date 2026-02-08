package com.sprint.mission.discodeit.dto;

public record UserCreateDto(
        String username,
        String email,
        String password
) {}