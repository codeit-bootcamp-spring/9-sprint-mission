package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(com.sprint.mission.discodeit.config.JpaAuditConfig.class)
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ReadStatusRepository readStatusRepository;

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

    // 멘토님 피드백 반영: Repository를 이용해 데이터 세팅 Flow 진행

    // 1. Given (데이터 세팅)
    // 유저 생성
    User testUser = new User("테스터", "test@test.com", "pass123", null);
    userRepository.save(testUser);

    // 채널 2개 생성
    Channel channel1 = new Channel("채널1", "설명1", ChannelType.PUBLIC);
    Channel channel2 = new Channel("채널2", "설명2", ChannelType.PRIVATE);
    channelRepository.saveAll(List.of(channel1, channel2));

    // 유저를 채널 1, 2에 모두 참여시킴 (ReadStatus 저장)
    ReadStatus status1 = new ReadStatus(testUser, channel1, Instant.now());
    ReadStatus status2 = new ReadStatus(testUser, channel2, Instant.now());
    readStatusRepository.saveAll(List.of(status1, status2));

    // 페이징 조건 설정 (0페이지, 10개씩)
    PageRequest pageRequest = PageRequest.of(0, 10);

    // 2. When (실제 조회 로직)
    // findAll()이 아니라, 방금 만든 특정 유저(testUser)의 ID로 채널을 조회
    Slice<Channel> result = channelRepository.findAllByUserId(testUser.getId(), pageRequest);

    // 3. Then (검증)
    assertThat(result).isNotEmpty();
    assertThat(result.getContent()).hasSize(2);
  }
}