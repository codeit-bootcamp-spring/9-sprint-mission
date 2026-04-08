package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private ReadStatusRepository readStatusRepository;

  @Mock
  private ChannelMapper channelMapper;

  @Mock
  private com.sprint.mission.discodeit.mapper.PageResponseMapper pageResponseMapper;

  @InjectMocks
  private BasicChannelService channelService;

  @Test
  @DisplayName("공개(PUBLIC) 채널 생성 성공 테스트")
  void create_public_channel_success() {
    // Given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("공개방", "방 설명");
    Channel mockChannel = org.mockito.Mockito.mock(Channel.class);

    // 서비스가 save 후 Channel 엔티티를 반환
    given(channelRepository.save(any(Channel.class))).willReturn(mockChannel);

    // When
    Channel result = channelService.create(request);

    // Then
    assertThat(result).isNotNull();
    verify(channelRepository).save(any(Channel.class));
  }

  @Test
  @DisplayName("비공개(PRIVATE) 채널 생성 성공 테스트")
  void create_private_channel_success() {
    // Given
    UUID userId = UUID.randomUUID();
    List<UUID> participantIds = List.of(userId);
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

    Channel mockChannel = org.mockito.Mockito.mock(Channel.class);
    User mockUser = org.mockito.Mockito.mock(User.class);

    // 비공개 채널 로직에 필요한 가짜 대답들
    given(channelRepository.save(any(Channel.class))).willReturn(mockChannel);
    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

    // When
    Channel result = channelService.create(request);

    // Then
    assertThat(result).isNotNull();
    verify(channelRepository).save(any(Channel.class));
  }

  @Test
  @DisplayName("채널 수정 성공 테스트 - 공개 채널")
  void update_public_channel_success() {
    // Given
    UUID channelId = UUID.randomUUID();
    // PublicChannelUpdateRequest 규격에 맞게 준비 (newName, newDescription)
    com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest request =
        new com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest("새이름", "새설명");

    Channel mockChannel = org.mockito.Mockito.mock(Channel.class);
    // 공개 채널이어야 수정이 가능하므로 타입을 PUBLIC으로 설정한 척
    given(mockChannel.getType()).willReturn(com.sprint.mission.discodeit.entity.ChannelType.PUBLIC);
    given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));
    given(channelRepository.save(any(Channel.class))).willReturn(mockChannel);

    // When
    Channel result = channelService.update(channelId, request);

    // Then
    assertThat(result).isNotNull();
    verify(channelRepository).save(any(Channel.class));
  }

  @Test
  @DisplayName("채널 수정 실패 테스트 - 비공개 채널은 수정 불가")
  void update_fail_private_channel() {
    // Given
    UUID channelId = UUID.randomUUID();
    com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest request =
        new com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest("새이름", "새설명");

    Channel mockChannel = org.mockito.Mockito.mock(Channel.class);
    // 비공개 채널인 경우 에러가 터지는지 확인하기 위해 타입을 PRIVATE으로 설정
    given(mockChannel.getType()).willReturn(
        com.sprint.mission.discodeit.entity.ChannelType.PRIVATE);
    given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));

    // When & Then
    org.assertj.core.api.Assertions.assertThatThrownBy(
            () -> channelService.update(channelId, request))
        .isInstanceOf(com.sprint.mission.discodeit.exception.ChannelException.class);
  }

  @Test
  @DisplayName("채널 삭제 성공 테스트 - 연관 데이터 삭제 포함")
  void delete_channel_success() {
    // Given
    UUID channelId = UUID.randomUUID();
    Channel mockChannel = org.mockito.Mockito.mock(Channel.class);

    // 삭제 전 존재 여부 확인 로직을 위해 findById 준비
    given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));

    // When
    channelService.delete(channelId);

    // Then
    verify(messageRepository).deleteAllByChannel_Id(any());
    verify(readStatusRepository).deleteAllByChannel_Id(any());
    verify(channelRepository).deleteById(channelId);
  }

  @Test
  @DisplayName("유저 ID로 참여 중인 채널 목록 조회 테스트")
  void find_all_channels_by_user_id() {
    // Given
    UUID userId = UUID.randomUUID();
    int page = 0;

    // Mock 대신 진짜 데이터가 담긴 SliceImpl 객체를 만듭니다.
    Channel mockChannel = org.mockito.Mockito.mock(Channel.class);
    org.springframework.data.domain.Slice<Channel> channelSlice =
        new org.springframework.data.domain.SliceImpl<>(java.util.List.of(mockChannel));

    given(channelRepository.findAllByUserId(any(UUID.class),
        any(org.springframework.data.domain.Pageable.class)))
        .willReturn(channelSlice);

    // 가짜 페이지 응답 객체 준비
    PageResponse mockPageResponse = org.mockito.Mockito.mock(PageResponse.class);
    given(pageResponseMapper.fromSlice(any())).willReturn(mockPageResponse);

    // When
    PageResponse<ChannelDto> result = channelService.findAll(userId, page);

    // Then
    assertThat(result).isNotNull();
    verify(channelRepository).findAllByUserId(any(UUID.class),
        any(org.springframework.data.domain.Pageable.class));
  }
}