package com.sprint.mission.discodeit.dto;

public record UserProfileDto(
        String fileName,
        byte[] bytes,
        String contentType
) {}