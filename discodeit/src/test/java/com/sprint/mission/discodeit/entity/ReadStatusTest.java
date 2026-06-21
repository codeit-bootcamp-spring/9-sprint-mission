package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReadStatusTest {

  @Test
  @DisplayName("null이 전달되면 notificationEnabled 필드가 수정되지 않는다")
  void update_whenNewNotificationEnabledIsNull_doesNotModifyField() {
    // given
    User user = new User("username", "email@test.com", "password", null);
    Channel channel = new Channel(ChannelType.PUBLIC, "name", "desc");
    Instant lastReadAt = Instant.now();
    ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
    
    // Set notificationEnabled to true initially
    readStatus.update(null, true);
    assertThat(readStatus.isNotificationEnabled()).isTrue();

    // when
    readStatus.update(null, null);

    // then
    assertThat(readStatus.isNotificationEnabled()).isTrue();
  }

  @Test
  @DisplayName("true 또는 false가 전달되면 notificationEnabled 필드가 알맞게 수정된다")
  void update_whenNewNotificationEnabledIsNotNull_modifiesField() {
    // given
    User user = new User("username", "email@test.com", "password", null);
    Channel channel = new Channel(ChannelType.PUBLIC, "name", "desc");
    Instant lastReadAt = Instant.now();
    ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);

    // when -> update to false
    readStatus.update(null, false);
    // then
    assertThat(readStatus.isNotificationEnabled()).isFalse();

    // when -> update to true
    readStatus.update(null, true);
    // then
    assertThat(readStatus.isNotificationEnabled()).isTrue();
  }
}
