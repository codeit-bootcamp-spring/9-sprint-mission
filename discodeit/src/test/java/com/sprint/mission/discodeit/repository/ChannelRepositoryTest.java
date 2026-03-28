package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;


  @Test
  @DisplayName("save channel success")
  void saveChannel_success() {

    Channel channel = new Channel(ChannelType.PUBLIC, "channel1", "description1");


    Channel saved = channelRepository.save(channel);


    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getType()).isEqualTo(ChannelType.PUBLIC);
    assertThat(saved.getName()).isEqualTo("channel1");
  }

  @Test
  @DisplayName("find channel by id success")
  void findChannelById_success() {

    Channel channel = channelRepository.save(
        new Channel(ChannelType.PUBLIC, "channel1", "description1")
    );

    Channel result = channelRepository.findById(channel.getId()).orElse(null);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(channel.getId());
  }

  @Test
  @DisplayName("find channel by id fail")
  void findChannelById_fail() {

    UUID randomId = UUID.randomUUID();

    boolean exists = channelRepository.findById(randomId).isPresent();

    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("find all by type success")
  void findAllByType_success() {

    channelRepository.save(new Channel(ChannelType.PUBLIC, "A", "A"));
    channelRepository.save(new Channel(ChannelType.PRIVATE, "B", "B"));

    List<Channel> result =
        channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of());

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getType()).isEqualTo(ChannelType.PUBLIC);
  }

  @Test
  @DisplayName("find all by type or id success")
  void findAllByTypeOrIdIn_success() {

    Channel ch1 = channelRepository.save(new Channel(ChannelType.PUBLIC, "A", "A"));
    Channel ch2 = channelRepository.save(new Channel(ChannelType.PRIVATE, "B", "B"));

    List<Channel> result =
        channelRepository.findAllByTypeOrIdIn(
            ChannelType.PUBLIC,
            List.of(ch2.getId())
        );

    assertThat(result).hasSize(2);
  }

  @Test
  @DisplayName("find all by type or id fail")
  void findAllByTypeOrIdIn_fail() {

    channelRepository.save(new Channel(ChannelType.PUBLIC, "A", "A"));

    List<Channel> result =
        channelRepository.findAllByTypeOrIdIn(
            ChannelType.PRIVATE,
            List.of()
        );

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("paging success")
  void paging_success() {

    for (int i = 0; i < 10; i++) {
      channelRepository.save(
          new Channel(ChannelType.PUBLIC, "channel" + i, "desc")
      );
    }

    Pageable pageable = PageRequest.of(0, 5);

    Page<Channel> result = channelRepository.findAll(pageable);

    assertThat(result.getContent().size()).isEqualTo(5);
    assertThat(result.getTotalElements()).isEqualTo(10);
  }

  @Test
  @DisplayName("paging second page success")
  void paging_secondPage_success() {

    for (int i = 0; i < 10; i++) {
      channelRepository.save(
          new Channel(ChannelType.PUBLIC, "channel" + i, "desc")
      );
    }

    Pageable pageable = PageRequest.of(1, 5);

    Page<Channel> result = channelRepository.findAll(pageable);

    assertThat(result.getContent().size()).isEqualTo(5);
  }
}