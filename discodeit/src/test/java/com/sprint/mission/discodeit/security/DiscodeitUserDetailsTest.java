package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.UserRole;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DiscodeitUserDetailsTest {

  @Test
  @DisplayName("equals/hashCode: 같은 사용자 ID면 같은 principal로 판단한다")
  void equalsAndHashCode_sameUserId() {
    UUID userId = UUID.randomUUID();
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(
        new UserResponse(userId, "jun", "jun@test.com", null, false, UserRole.USER),
        "encodedPassword"
    );
    DiscodeitUserDetails sameUserDetails = new DiscodeitUserDetails(
        new UserResponse(userId, "jun-updated", "changed@test.com", null, true,
            UserRole.CHANNEL_MANAGER),
        "changedPassword"
    );

    assertThat(userDetails)
        .isEqualTo(sameUserDetails)
        .hasSameHashCodeAs(sameUserDetails);
  }

  @Test
  @DisplayName("equals/hashCode: 사용자 ID가 다르면 다른 principal로 판단한다")
  void equalsAndHashCode_differentUserId() {
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(
        new UserResponse(UUID.randomUUID(), "jun", "jun@test.com", null, false, UserRole.USER),
        "encodedPassword"
    );
    DiscodeitUserDetails otherUserDetails = new DiscodeitUserDetails(
        new UserResponse(UUID.randomUUID(), "min", "min@test.com", null, false, UserRole.USER),
        "encodedPassword"
    );

    assertThat(userDetails).isNotEqualTo(otherUserDetails);
  }
}
