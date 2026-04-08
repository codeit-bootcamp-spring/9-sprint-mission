package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserStatusAlreadyExistsException extends UserStatusException {

    public UserStatusAlreadyExistsException(Map<String, Object> details) {
        super(ErrorCode.DUPLICATE_USER_STATUS, details);
    }

    public static UserStatusAlreadyExistsException withUserId(UUID userId) {
        return new UserStatusAlreadyExistsException(Map.of("userId", userId));
    }
}
