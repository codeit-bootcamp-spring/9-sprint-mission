package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UpdateUserDto(
        UUID id, String reName, String rePassword, String reMail, String rePhoneNumber, UUID reProfileId
) {
}
