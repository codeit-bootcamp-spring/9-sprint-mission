package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  @DisplayName("채널 저장 및 ID로 조회 - 성공")
  void saveAndFindById_Success() {
    Channel channel = new Channel(ChannelType.PUBLIC, "테스트 채널", "테스트 설명입니다");
    Channel savedChannel = channelRepository.save(channel);

    Optional<Channel> foundChannel = channelRepository.findById(savedChannel.getId());

    assertThat(foundChannel).isPresent();
    assertThat(foundChannel.get().getName()).isEqualTo("테스트 채널");
    assertThat(foundChannel.get().getType()).isEqualTo(ChannelType.PUBLIC);
  }

  @Test
  @DisplayName("존재하지 않는 채널 ID로 조회 - 실패 (빈 Optional 반환)")
  void findById_Fail_NotFound() {
    UUID randomId = UUID.randomUUID();

    Optional<Channel> foundChannel = channelRepository.findById(randomId);

    assertThat(foundChannel).isEmpty();
  }

}