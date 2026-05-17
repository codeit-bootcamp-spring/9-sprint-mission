package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.data.UserDto;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DiscodeitUserDetailsTest {

  @Test
  void equalsAndHashCode_UseUserId() {
    UUID userId = UUID.randomUUID();
    DiscodeitUserDetails first = new DiscodeitUserDetails(
        new UserDto(userId, "first", "first@example.com", null, false), "password");
    DiscodeitUserDetails second = new DiscodeitUserDetails(
        new UserDto(userId, "second", "second@example.com", null, true), "other");

    assertThat(first).isEqualTo(second);
    assertThat(first.hashCode()).isEqualTo(second.hashCode());
  }
}
