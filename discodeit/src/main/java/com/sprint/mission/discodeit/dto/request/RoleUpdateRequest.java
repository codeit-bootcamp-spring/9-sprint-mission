package com.sprint.mission.discodeit.dto.request;

<<<<<<<< HEAD:discodeit/src/main/java/com/sprint/mission/discodeit/dto/request/RoleUpdateRequest.java
import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;

public record RoleUpdateRequest(
    UUID userId,
    Role newRole
========
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(
    @NotNull(message = "사용자 ID는 필수입니다")
    UUID userId,
    
    @NotNull(message = "마지막 활동 시간은 필수입니다")
    @PastOrPresent(message = "마지막 활동 시간은 현재 또는 과거 시간이어야 합니다")
    Instant lastActiveAt
>>>>>>>> 111fa54a2a44314b37815e9f3124bf738e7b4103:discodeit/src/main/java/com/sprint/mission/discodeit/dto/request/UserStatusCreateRequest.java
) {

}
