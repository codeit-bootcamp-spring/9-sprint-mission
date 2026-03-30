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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  @DisplayName("저장후 ID 조회")
  void saveAndFindByIdSuccess() {
    Channel channel = new Channel(ChannelType.PUBLIC, "공개채널", "누구나 참여 가능");
    Channel saved = channelRepository.save(channel);

    Optional<Channel> found = channelRepository.findById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("공개채널");
    assertThat(found.get().getType()).isEqualTo(ChannelType.PUBLIC);
  }

  @Test
  @DisplayName("모든 채널 조회")
  void findAllPagingSuccess() {
    channelRepository.save(new Channel(ChannelType.PUBLIC, "채널1", "설명1"));
    channelRepository.save(new Channel(ChannelType.PRIVATE, "채널2", "설명2"));
    channelRepository.save(new Channel(ChannelType.PUBLIC, "채널3", "설명3"));

    PageRequest pageRequest = PageRequest.of(0, 2);

    Page<Channel> result = channelRepository.findAll(pageRequest);

    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getTotalElements()).isEqualTo(3);
  }

  @Test
  @DisplayName("없는 채널 조회")
  void findByIdFail() {
    Optional<Channel> found = channelRepository.findById(UUID.randomUUID());
    assertThat(found).isEmpty();
  }
}