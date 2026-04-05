package com.sprint.mission.discodeit.unit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.base.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.type.ChannelType;
import java.time.Instant;
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
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ChannelMapper channelMapper;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;

  @InjectMocks
  private BasicChannelService channelService;


  @Test
  @DisplayName("Public 채널 생성 성공")
  void publicChannelCreateSuccess() {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("test", "test channel");
    ChannelDto expectDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "test", "test channel", null, Instant.now());

    given(channelRepository.save(any(Channel.class))).willAnswer(invocation -> invocation.getArgument(0));
    given(channelMapper.toDto(any(Channel.class))).willReturn(expectDto);

    ChannelDto response = channelService.createPublicChannel(request);

    assertThat(response.name()).isEqualTo(expectDto.name());
    then(channelRepository).should().save(any(Channel.class));
  }

  @Test
  @DisplayName("Private 채널 생성 성공")
  void privateChannelCreateSuccess() {
    UUID id = UUID.randomUUID();
    List<UUID> participantIds = List.of(id);
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

    given(channelRepository.save(any(Channel.class))).willAnswer(invocation -> invocation.getArgument(0));

    ChannelDto expectDto = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, "private test", "private desc", null, Instant.now());
    given(channelMapper.toDto(any(Channel.class))).willReturn(expectDto);

    ChannelDto response = channelService.createPrivateChannel(request);

    assertThat(response.name()).isEqualTo(expectDto.name());
    then(channelRepository).should().save(any(Channel.class));
  }

  @Test
  @DisplayName("채널 수정 테스트 성공")
  void updateChannelUpdateSuccess() {
    UUID id = UUID.randomUUID();
    ChannelUpdateRequest request = new ChannelUpdateRequest("new name", "new description");
    Channel channel = new Channel(ChannelType.PUBLIC, "test", "test channel"); // 실제 객체 사용

    given(channelRepository.findById(id)).willReturn(Optional.of(channel));

    channelService.update(id, request);

    assertThat(channel.getName()).isEqualTo("new name");
    assertThat(channel.getDescription()).isEqualTo("new description");
    then(channelRepository).should().findById(id);
  }

  // 4. Private 채널 수정 시도 실패
  @Test
  @DisplayName("Private 채널 수정 시도 시 예외 발생")
  void updatePrivateUpdateFail() {
    // given
    UUID id = UUID.randomUUID();
    ChannelUpdateRequest request = new ChannelUpdateRequest("new name", "new description");
    Channel channel = new Channel(ChannelType.PRIVATE, "test", "test channel");

    given(channelRepository.findById(id)).willReturn(Optional.of(channel));

    // when & then
    assertThatThrownBy(() -> channelService.update(id, request))
        .isInstanceOf(PrivateChannelUpdateException.class)
        .hasMessage(ErrorCode.PRIVATE_CHANNEL_UPDATE.getMessage());
  }

  @Test
  @DisplayName("채널 삭제 테스트 성공")
  void deleteChannelSuccess() {
    UUID id = UUID.randomUUID();
    Channel channel = mock(Channel.class);

    given(channelRepository.findById(id)).willReturn(Optional.of(channel));

    channelService.delete(id);

    then(channelRepository).should().delete(channel);
  }

  @Test
  @DisplayName("채널 삭제 테스트 실패 - 해당 채널 없음")
  void deleteChannelFail() {
    UUID id = UUID.randomUUID();
    given(channelRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.delete(id))
        .isInstanceOf(ChannelNotFoundException.class)
        .hasMessage(ErrorCode.CHANNEL_NOT_FOUND.getMessage());

    then(channelRepository).should(never()).delete(any(Channel.class));
  }

  @Test
  @DisplayName("채널 검색 테스트 성공")
  void findChannelSuccess(){
    UUID id = UUID.randomUUID();
    String channelName = "test name";
    String channelDescription = "test description";
    Channel channel = new Channel(ChannelType.PUBLIC, channelName, channelDescription);

    ChannelDto expectedDto = new ChannelDto(id, ChannelType.PUBLIC, channelName, channelDescription, null, Instant.now());

    given(channelRepository.findById(id)).willReturn(Optional.of(channel));
    given(channelMapper.toDto(channel)).willReturn(expectedDto);

    ChannelDto response = channelService.findByID(id);

    assertThat(response.id()).isEqualTo(id);
    assertThat(response.name()).isEqualTo(channelName);

    then(channelRepository).should().findById(id);
  }

  @Test
  @DisplayName("ID로 채널 조회 실패 - 해당 채널 없음")
  void findChannelByIdFail() {
    UUID id = UUID.randomUUID();
    given(channelRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.findByID(id))
        .isInstanceOf(ChannelNotFoundException.class)
        .hasMessage(ErrorCode.CHANNEL_NOT_FOUND.getMessage());

    then(channelMapper).shouldHaveNoInteractions();
  }
}
