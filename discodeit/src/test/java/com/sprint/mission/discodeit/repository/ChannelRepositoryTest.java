package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(com.sprint.mission.discodeit.config.JpaAuditConfig.class)
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  @DisplayName("공개 채널 생성 및 타입 확인 테스트")
  void create_and_check_channel_type() {

    // Given
    Channel channel = new Channel("공개 채팅방", "설명", ChannelType.PUBLIC);

    // When
    Channel savedChannel = channelRepository.save(channel);

    // Then
    assertThat(savedChannel.getId()).isNotNull();
    assertThat(savedChannel.getType()).isEqualTo(ChannelType.PUBLIC);
  }

  @Test
  @DisplayName("커스텀 쿼리 테스트 - 특정 유저의 채널 목록 페이징 조회")
  void find_channels_with_paging() {
    // Given
    // 테스트를 위해 채널 1개를 저장해둡니다.
    channelRepository.save(new Channel("테스트 채널", "설명", ChannelType.PUBLIC));

    // 페이징 조건 (0페이지, 10개씩)
    PageRequest pageRequest = PageRequest.of(0, 10);

    // When
    Slice<Channel> result = channelRepository.findAll(pageRequest);

    // Then
    assertThat(result).isNotEmpty();
    assertThat(result.getSize()).isEqualTo(10);
  }
}