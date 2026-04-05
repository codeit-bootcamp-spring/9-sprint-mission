package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.*;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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

  private Channel publicChannel;
  private Channel privateChannel;

  @BeforeEach
  void setUp() {
    publicChannel = new Channel(ChannelType.PUBLIC, "공개채널", "공개채널 설명");
    privateChannel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.save(publicChannel);
    channelRepository.save(privateChannel);
  }

  @Test
  @DisplayName("PUBLIC 타입으로 채널 조회 - 성공")
  void findAllByTypeOrIdIn_byType_success() {
    // when
    List<Channel> result = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PUBLIC,
        List.of()
    );

    // then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getType()).isEqualTo(ChannelType.PUBLIC);
  }

  @Test
  @DisplayName("ID로 채널 조회 - 성공")
  void findAllByTypeOrIdIn_byId_success() {
    // given
    UUID privateChannelId = privateChannel.getId();

    // when
    List<Channel> result = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PUBLIC,
        List.of(privateChannelId)
    );

    // then
    assertThat(result).hasSize(2); // PUBLIC 채널 + PRIVATE 채널
  }

  @Test
  @DisplayName("존재하지 않는 ID + PRIVATE 타입으로 채널 조회 - PUBLIC만 반환")
  void findAllByTypeOrIdIn_notFound() {
    // when
    // PUBLIC 타입 + 존재하지 않는 ID
    List<Channel> result = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PUBLIC,
        List.of(UUID.randomUUID())  // 존재하지 않는 ID
    );

    // then
    // PUBLIC 채널 1개만 나와야 함
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getType()).isEqualTo(ChannelType.PUBLIC);
  }
}