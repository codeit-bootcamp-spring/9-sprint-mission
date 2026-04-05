package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  @DisplayName("타입 또는 ID 목록으로 채널 조회 - 성공 (조건에 맞는 데이터 존재)")
  void findAllByTypeOrIdIn_Success() {
    // Given
    Channel publicCh = channelRepository.save(new Channel(ChannelType.PUBLIC, "공개방", "설명"));
    Channel privateCh = channelRepository.save(new Channel(ChannelType.PRIVATE, "비공개방", "설명"));

    // When
    List<Channel> result = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PUBLIC, List.of(privateCh.getId())
    );

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).extracting("name").containsExactlyInAnyOrder("공개방", "비공개방");
  }

  @Test
  @DisplayName("타입 또는 ID 목록으로 채널 조회 - 실패 (조건에 맞는 데이터 없음)")
  void findAllByTypeOrIdIn_Fail() {
    // Given
    channelRepository.save(new Channel(ChannelType.PUBLIC, "공개방", "설명"));

    // When
    List<Channel> result = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PRIVATE, List.of(UUID.randomUUID())
    );

    // Then
    assertThat(result).isEmpty();
  }
}