package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.util.Collections;
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

  @Mock private ChannelRepository channelRepository;
  @Mock private ReadStatusRepository readStatusRepository;
  @Mock private MessageRepository messageRepository;
  @Mock private UserRepository userRepository;
  @Mock private ChannelMapper channelMapper;

  @InjectMocks private BasicChannelService basicChannelService;

  @Test
  @DisplayName("퍼블릭 채널 생성 성공")
  void create_PublicChannel_Success() {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지사항", "설명");

    basicChannelService.create(request);

    then(channelRepository).should(times(1)).save(any(Channel.class));
  }

  @Test
  @DisplayName("퍼블릭 채널 생성 실패 - 데이터베이스 저장 오류")
  void create_PublicChannel_Fail_DBError() {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지사항", "설명");
    given(channelRepository.save(any(Channel.class))).willThrow(new RuntimeException("DB 저장 실패"));

    assertThrows(RuntimeException.class, () -> basicChannelService.create(request));
  }

  @Test
  @DisplayName("프라이빗 채널 생성 성공")
  void create_PrivateChannel_Success() {
    List<UUID> participantIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

    given(userRepository.findAllById(participantIds)).willReturn(
        List.of(new User("user1", "e@e.com", "pw", null), new User("user2", "a@a.com", "pw", null))
    );

    basicChannelService.create(request);

    then(channelRepository).should(times(1)).save(any(Channel.class));
    then(readStatusRepository).should(times(1)).saveAll(any());
  }

  @Test
  @DisplayName("프라이빗 채널 생성 실패 - 참여자 정보가 없음")
  void create_PrivateChannel_Fail_NoParticipants() {
    List<UUID> invalidParticipantIds = List.of(UUID.randomUUID());
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(invalidParticipantIds);

    given(userRepository.findAllById(invalidParticipantIds)).willReturn(Collections.emptyList());
    given(readStatusRepository.saveAll(any())).willThrow(new IllegalArgumentException("참여자 상태 정보 저장 실패"));

    assertThrows(IllegalArgumentException.class, () -> basicChannelService.create(request));
  }

  @Test
  @DisplayName("퍼블릭 채널 수정 성공")
  void update_Success_PublicChannel() {
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새이름", "새설명");
    Channel mockChannel = new Channel(ChannelType.PUBLIC, "기존", "기존");

    given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));

    basicChannelService.update(channelId, request);

    assertThat(mockChannel.getName()).isEqualTo("새이름");
    then(channelRepository).should(times(1)).save(mockChannel);
  }

  @Test
  @DisplayName("채널 수정 실패 - 존재하지 않는 채널")
  void update_Fail_ChannelNotFound() {
    UUID invalidChannelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새이름", "새설명");

    given(channelRepository.findById(invalidChannelId)).willReturn(Optional.empty());

    assertThrows(ChannelNotFoundException.class, () -> basicChannelService.update(invalidChannelId, request));
  }

  @Test
  @DisplayName("채널 수정 실패 - 프라이빗 채널은 수정 불가")
  void update_Fail_PrivateChannel() {
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새이름", "새설명");
    Channel mockChannel = new Channel(ChannelType.PRIVATE, null, null);

    given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));

    assertThrows(PrivateChannelUpdateException.class, () -> basicChannelService.update(channelId, request));
  }


  @Test
  @DisplayName("채널 삭제 성공")
  void delete_Success() {
    UUID channelId = UUID.randomUUID();
    Channel mockChannel = new Channel(ChannelType.PUBLIC, "이름", "설명");
    given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));

    basicChannelService.delete(channelId);

    then(messageRepository).should(times(1)).deleteAllByChannelId(any());
    then(readStatusRepository).should(times(1)).deleteAllByChannelId(any());
    then(channelRepository).should(times(1)).deleteById(channelId);
  }

  @Test
  @DisplayName("채널 삭제 실패 - 존재하지 않는 채널")
  void delete_Fail_ChannelNotFound() {
    UUID invalidChannelId = UUID.randomUUID();
    given(channelRepository.findById(invalidChannelId)).willReturn(Optional.empty());

    assertThrows(ChannelNotFoundException.class, () -> basicChannelService.delete(invalidChannelId));

    then(messageRepository).should(times(0)).deleteAllByChannelId(any());
    then(channelRepository).should(times(0)).deleteById(any());
  }

  @Test
  @DisplayName("유저 ID로 채널 목록 조회 성공")
  void findAllByUserId_Success() {
    UUID userId = UUID.randomUUID();

    Channel publicChannel = new Channel(ChannelType.PUBLIC, "공지", "설명");
    Channel myPrivateChannel = new Channel(ChannelType.PRIVATE, null, null);
    Channel otherPrivateChannel = new Channel(ChannelType.PRIVATE, null, null);

    User me = new User("me", "m@m.com", "123", null);
    ReadStatus myStatus = new ReadStatus(me, myPrivateChannel, null);

    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(myStatus));
    given(channelRepository.findAll()).willReturn(List.of(publicChannel, myPrivateChannel, otherPrivateChannel));

    basicChannelService.findAllByUserId(userId);

    then(readStatusRepository).should(times(1)).findAllByUserId(userId);
    then(channelRepository).should(times(1)).findAll();
  }

  @Test
  @DisplayName("유저 ID로 채널 목록 조회 성공")
  void findAllByUserId_Success_EmptyList() {
    UUID userId = UUID.randomUUID();

    given(readStatusRepository.findAllByUserId(userId)).willReturn(Collections.emptyList());
    given(channelRepository.findAll()).willReturn(Collections.emptyList());

    basicChannelService.findAllByUserId(userId);

    then(readStatusRepository).should(times(1)).findAllByUserId(userId);
    then(channelRepository).should(times(1)).findAll();
  }
}