package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelMapper channelMapper;

  @InjectMocks
  private BasicChannelService basicChannelService;

  @DisplayName("create (PUBLIC) - 성공")
  @Test
  void createPublicChannel_Success() {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("Public Channel", "Desc");
    Channel savedChannel = new Channel(ChannelType.PUBLIC, "Public Channel", "Desc");
    ChannelDto expectedDto = new ChannelDto(savedChannel.getId(), ChannelType.PUBLIC, "Public Channel", "Desc", List.of(), null);
    
    given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);

    // when
    ChannelDto result = basicChannelService.create(request);

    // then
    assertThat(result.name()).isEqualTo("Public Channel");
    then(channelRepository).should().save(any(Channel.class));
  }

  @DisplayName("create (PRIVATE) - 성공")
  @Test
  void createPrivateChannel_Success() {
    // given
    UUID user1Id = UUID.randomUUID();
    UUID user2Id = UUID.randomUUID();
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(user1Id, user2Id));
    
    User user1 = new User("user1", "1@test.com", "pass", null);
    User user2 = new User("user2", "2@test.com", "pass", null);
    
    Channel savedChannel = new Channel(ChannelType.PRIVATE, null, null);
    ChannelDto expectedDto = new ChannelDto(savedChannel.getId(), ChannelType.PRIVATE, null, null, List.of(), null);
    
    given(userRepository.findAllById(request.participantIds())).willReturn(List.of(user1, user2));
    given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);

    // when
    ChannelDto result = basicChannelService.create(request);

    // then
    assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
    then(channelRepository).should().save(any(Channel.class));
    then(readStatusRepository).should().saveAll(any());
  }

  @DisplayName("update - 성공 (PUBLIC 채널)")
  @Test
  void update_Success_PublicChannel() {
    // given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("New Name", "New Desc");
    Channel existingChannel = new Channel(ChannelType.PUBLIC, "Old Name", "Old Desc");
    ChannelDto expectedDto = new ChannelDto(channelId, ChannelType.PUBLIC, "New Name", "New Desc", List.of(), null);
    
    given(channelRepository.findById(channelId)).willReturn(Optional.of(existingChannel));
    given(channelMapper.toDto(existingChannel)).willReturn(expectedDto);

    // when
    ChannelDto result = basicChannelService.update(channelId, request);

    // then
    assertThat(result.name()).isEqualTo("New Name");
    assertThat(existingChannel.getName()).isEqualTo("New Name");
  }

  @DisplayName("update - 실패 (PRIVATE 채널은 수정 불가)")
  @Test
  void update_Fail_PrivateChannel() {
    // given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("New Name", "New Desc");
    Channel existingChannel = new Channel(ChannelType.PRIVATE, null, null);
    
    given(channelRepository.findById(channelId)).willReturn(Optional.of(existingChannel));

    // when & then
    assertThatThrownBy(() -> basicChannelService.update(channelId, request))
        .isInstanceOf(PrivateChannelUpdateException.class);
  }

  @DisplayName("delete - 성공")
  @Test
  void delete_Success() {
    // given
    UUID channelId = UUID.randomUUID();
    given(channelRepository.existsById(channelId)).willReturn(true);

    // when
    basicChannelService.delete(channelId);

    // then
    then(messageRepository).should().deleteAllByChannelId(channelId);
    then(readStatusRepository).should().deleteAllByChannelId(channelId);
    then(channelRepository).should().deleteById(channelId);
  }

  @DisplayName("delete - 실패 (존재하지 않는 채널)")
  @Test
  void delete_Fail_ChannelNotFound() {
    // given
    UUID channelId = UUID.randomUUID();
    given(channelRepository.existsById(channelId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> basicChannelService.delete(channelId))
        .isInstanceOf(ChannelNotFoundException.class);
        
    then(channelRepository).should(never()).deleteById(any());
  }

  @DisplayName("findAllByUserId - 성공")
  @Test
  void findAllByUserId_Success() {
    // given
    UUID userId = UUID.randomUUID();
    User dummyUser = new User("test", "test@test.com", "pass", null);
    Channel publicChannel = new Channel(ChannelType.PUBLIC, "Public", "Desc");
    Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
    
    ReadStatus readStatus = new ReadStatus(dummyUser, privateChannel, null);
    
    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(readStatus));
    
    List<Channel> mockResult = new ArrayList<>();
    mockResult.add(publicChannel);
    mockResult.add(privateChannel);
    
    // JPA Entity의 ID는 생성 시 null이므로 List.of(null)이 들어가서 NPE가 발생할 수 있습니다.
    // ArgumentMatchers.anyList()를 명시적으로 사용하여 어떤 리스트가 와도 매핑되게 처리합니다.
    given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList()))
        .willReturn(mockResult);
        
    given(channelMapper.toDto(publicChannel))
        .willReturn(new ChannelDto(publicChannel.getId(), ChannelType.PUBLIC, "Public", "Desc", List.of(), null));
    given(channelMapper.toDto(privateChannel))
        .willReturn(new ChannelDto(privateChannel.getId(), ChannelType.PRIVATE, null, null, List.of(), null));

    // when
    List<ChannelDto> result = basicChannelService.findAllByUserId(userId);

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).type()).isEqualTo(ChannelType.PUBLIC);
    assertThat(result.get(1).type()).isEqualTo(ChannelType.PRIVATE);
  }
}
