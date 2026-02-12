package com.sprint.mission.discodeit.dto;
import java.util.UUID;
public record UserUpdateRequest(String username, String email, String phoneNumber, UUID profileId) {}