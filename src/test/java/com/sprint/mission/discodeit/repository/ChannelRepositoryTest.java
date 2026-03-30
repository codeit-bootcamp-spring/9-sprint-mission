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
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(RepositoryTestConfig.class)
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  void findAllByTypeOrIdIn_shouldReturnPublicAndSpecificChannels_whenConditionMatches() {
    Channel publicChannel = channelRepository.save(new Channel(ChannelType.PUBLIC, "pub", "public"));
    Channel privateChannel = channelRepository.save(new Channel(ChannelType.PRIVATE, "pri", "private"));

    List<Channel> result = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PUBLIC,
        List.of(privateChannel.getId())
    );

    assertThat(result)
        .hasSize(2)
        .extracting(Channel::getId)
        .containsExactlyInAnyOrder(publicChannel.getId(), privateChannel.getId());
  }

  @Test
  void findAllByTypeOrIdIn_shouldReturnEmptyList_whenNoConditionMatches() {
    channelRepository.save(new Channel(ChannelType.PRIVATE, "pri", "private"));

    List<Channel> result = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PUBLIC,
        List.of(UUID.randomUUID())
    );

    assertThat(result).isEmpty();
  }

  @Test
  void findAll_shouldReturnSortedPage_whenPagingAndSortingApplied() {
    channelRepository.save(new Channel(ChannelType.PUBLIC, "gamma", "d"));
    channelRepository.save(new Channel(ChannelType.PUBLIC, "alpha", "d"));
    channelRepository.save(new Channel(ChannelType.PUBLIC, "beta", "d"));

    Page<Channel> page = channelRepository.findAll(
        PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name"))
    );

    assertThat(page.getContent())
        .hasSize(2)
        .extracting(Channel::getName)
        .containsExactly("alpha", "beta");
  }

  @Test
  void findAll_shouldReturnEmptyPage_whenPageOutOfRange() {
    channelRepository.save(new Channel(ChannelType.PUBLIC, "alpha", "d"));

    Page<Channel> page = channelRepository.findAll(
        PageRequest.of(10, 5, Sort.by("name"))
    );

    assertThat(page.getContent()).isEmpty();
  }
}