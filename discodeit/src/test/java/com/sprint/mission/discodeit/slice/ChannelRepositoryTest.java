package com.sprint.mission.discodeit.slice;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.type.ChannelType;
import java.util.List;
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
public class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ReadStatusRepository readStatusRepository; // 연관관계 저장을 위해 추가

  @Test
  @DisplayName("사용자 ID로 채널 목록 조회 성공")
  void findAllByUserId_Success() {
    User me = userRepository.save(new User("me", "pw", "me@test.com", null));
    User others = userRepository.save(new User("others", "pw", "others@test.com", null));

    Channel publicChannel = channelRepository.save(new Channel(ChannelType.PUBLIC, "public-room", "desc"));

    Channel myPrivateChannel = channelRepository.save(new Channel(ChannelType.PRIVATE, "my-private", "desc"));
    readStatusRepository.save(new ReadStatus(me, myPrivateChannel));

    Channel othersPrivateChannel = channelRepository.save(new Channel(ChannelType.PRIVATE, "others-private", "desc"));
    readStatusRepository.save(new ReadStatus(others, othersPrivateChannel));

    channelRepository.flush();

    List<Channel> result = channelRepository.findAllByUserId(me.getId());

    assertThat(result).hasSize(2);
    assertThat(result).extracting("name")
        .containsExactlyInAnyOrder("public-room", "my-private")
        .doesNotContain("others-private");
  }


  @Test
  @DisplayName("존재하지 않는 사용자 ID로 조회 실패")
  void findAllByUserId_fail() {
    Channel publicChannel = channelRepository.save(new Channel(ChannelType.PUBLIC, "public-room", "desc"));

    List<Channel> result = channelRepository.findAllByUserId(UUID.randomUUID());

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getType()).isEqualTo(ChannelType.PUBLIC);
  }

}
