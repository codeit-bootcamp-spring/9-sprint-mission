package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

  @InjectMocks
  private BasicChannelService channelService;

  @Mock private ChannelRepository channelRepository;
  @Mock private ReadStatusRepository readStatusRepository;
  @Mock private MessageRepository messageRepository;
  @Mock private UserRepository userRepository;
  @Mock private ChannelMapper channelMapper;

  // ==========================================
  // 1. Create 메소드 테스트
  // ==========================================
  @Test
  @DisplayName("공개(PUBLIC) 채널 생성 성공")
  void createPublicChannel_Success() {
    // Given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("일반채널", "테스트입니다");
    ChannelDto mockDto = mock(ChannelDto.class);
    given(channelMapper.toDto(any(Channel.class))).willReturn(mockDto);

    // When
    ChannelDto result = channelService.create(request);

    // Then
    assertThat(result).isNotNull();
    then(channelRepository).should(times(1)).save(any(Channel.class));
  }

  @Test
  @DisplayName("비공개(PRIVATE) 채널 생성 성공")
  void createPrivateChannel_Success() {
    // Given
    List<UUID> participantIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

    List<User> mockUsers = List.of(mock(User.class), mock(User.class));
    ChannelDto mockDto = mock(ChannelDto.class);

    given(userRepository.findAllById(participantIds)).willReturn(mockUsers);
    given(channelMapper.toDto(any(Channel.class))).willReturn(mockDto);

    // When
    ChannelDto result = channelService.create(request);

    // Then
    assertThat(result).isNotNull();
    then(channelRepository).should(times(1)).save(any(Channel.class));
    // 참여자 수만큼 ReadStatus가 저장되었는지 확인
    then(readStatusRepository).should(times(1)).saveAll(any());
  }

  // ==========================================
  // 2. Update 메소드 테스트
  // ==========================================
  @Test
  @DisplayName("채널 수정 성공 (PUBLIC 채널)")
  void update_Success() {
    // Given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새이름", "새설명");
    Channel publicChannel = new Channel(ChannelType.PUBLIC, "구이름", "구설명"); // 실제 객체 사용
    ChannelDto mockDto = mock(ChannelDto.class);

    given(channelRepository.findById(channelId)).willReturn(Optional.of(publicChannel));
    given(channelMapper.toDto(publicChannel)).willReturn(mockDto);

    // When
    ChannelDto result = channelService.update(channelId, request);

    // Then
    assertThat(result).isNotNull();
    assertThat(publicChannel.getName()).isEqualTo("새이름");
  }

  @Test
  @DisplayName("채널 수정 실패 (PRIVATE 채널은 수정 불가)")
  void update_Fail_PrivateChannel() {
    // Given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새이름", "새설명");
    Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);

    given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));

    // When & Then
    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(PrivateChannelUpdateException.class);
  }

  // ==========================================
  // 3. Delete 메소드 테스트
  // ==========================================
  @Test
  @DisplayName("채널 삭제 성공 (연관 데이터 포함)")
  void delete_Success() {
    // Given
    UUID channelId = UUID.randomUUID();
    given(channelRepository.existsById(channelId)).willReturn(true);

    // When
    channelService.delete(channelId);

    // Then
    then(messageRepository).should(times(1)).deleteAllByChannelId(channelId);
    then(readStatusRepository).should(times(1)).deleteAllByChannelId(channelId);
    then(channelRepository).should(times(1)).deleteById(channelId);
  }

  @Test
  @DisplayName("채널 삭제 실패 (존재하지 않는 채널)")
  void delete_Fail_NotFound() {
    // Given
    UUID channelId = UUID.randomUUID();
    given(channelRepository.existsById(channelId)).willReturn(false);

    // When & Then
    assertThatThrownBy(() -> channelService.delete(channelId))
        .isInstanceOf(ChannelNotFoundException.class);

    then(channelRepository).should(times(0)).deleteById(any());
  }

  // ==========================================
  // 4. findByUserId 메소드 테스트
  // ==========================================
  @Test
  @DisplayName("유저 ID로 채널 목록 조회 성공")
  void findAllByUserId_Success() {
    // Given
    UUID userId = UUID.randomUUID();
    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of()); // 빈 목록 반환 가정
    given(channelRepository.findAllByTypeOrIdIn(any(), any())).willReturn(List.of(mock(Channel.class)));
    given(channelMapper.toDto(any(Channel.class))).willReturn(mock(ChannelDto.class));

    // When
    List<ChannelDto> result = channelService.findAllByUserId(userId);

    // Then
    assertThat(result).isNotEmpty();
    then(channelRepository).should(times(1)).findAllByTypeOrIdIn(any(), any());
  }
}