package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
@DisplayName("ChannelRepository 슬라이스 테스트")
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("사용자별 접근 가능한 채널 조회 - 성공 (Public 및 가입된 Private)")
  void findAccessibleChannels_Success() {
    // given
    Channel publicChannel = new Channel(ChannelType.PUBLIC, "공개방", "누구나");
    entityManager.persist(publicChannel);

    Channel privateChannel = new Channel(ChannelType.PRIVATE, "비밀방", "초대만");
    entityManager.persist(privateChannel);
    entityManager.flush();
    entityManager.clear();

    // when
    List<Channel> channels = channelRepository.findAccessibleChannelsByUserId(UUID.randomUUID());

    // then
    assertThat(channels).extracting("type").contains(ChannelType.PUBLIC);
  }

  @Test
  @DisplayName("존재하지 않는 ID로 채널 조회 - 실패")
  void findById_Fail() {
    // given
    UUID nonExistentId = UUID.randomUUID();

    // when
    Optional<Channel> found = channelRepository.findById(nonExistentId);

    // then
    assertThat(found).isEmpty();
  }
}