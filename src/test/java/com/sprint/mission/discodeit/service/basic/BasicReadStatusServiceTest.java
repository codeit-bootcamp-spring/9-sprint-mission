package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
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
class BasicReadStatusServiceTest {

    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ReadStatusMapper readStatusMapper;

    @InjectMocks
    private BasicReadStatusService readStatusService;

    private final UUID userId = UUID.randomUUID();
    private final UUID channelId = UUID.randomUUID();
    private final UUID readStatusId = UUID.randomUUID();
    private final Instant now = Instant.now();

    @Test
    @DisplayName("읽음 상태 생성 - 신규")
    void create_success_new() {
        // given
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, now);
        User user = new User("user1", "user1@test.com", "password1", null);
        Channel channel = new Channel(ChannelType.PUBLIC, "channel1", null);
        ReadStatus readStatus = new ReadStatus(user, channel, now);
        ReadStatusDto expectedDto = new ReadStatusDto(readStatusId, userId, channelId, now);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(readStatusRepository.findByUser_IdAndChannel_Id(userId, channelId))
                .willReturn(Optional.empty());
        given(readStatusRepository.save(any(ReadStatus.class))).willReturn(readStatus);
        given(readStatusMapper.toDto(any(ReadStatus.class))).willReturn(expectedDto);

        // when
        ReadStatusDto result = readStatusService.create(request);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.channelId()).isEqualTo(channelId);
    }

    @Test
    @DisplayName("읽음 상태 생성 - 기존 것 반환")
    void create_success_existing() {
        // given
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, now);
        User user = new User("user1", "user1@test.com", "password1", null);
        Channel channel = new Channel(ChannelType.PUBLIC, "channel1", null);
        ReadStatus existingReadStatus = new ReadStatus(user, channel, now);
        ReadStatusDto expectedDto = new ReadStatusDto(readStatusId, userId, channelId, now);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(readStatusRepository.findByUser_IdAndChannel_Id(userId, channelId))
                .willReturn(Optional.of(existingReadStatus));
        given(readStatusMapper.toDto(existingReadStatus)).willReturn(expectedDto);

        // when
        ReadStatusDto result = readStatusService.create(request);

        // then
        assertThat(result).isNotNull();
        then(readStatusRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("읽음 상태 생성 실패 - 사용자 없음")
    void create_fail_userNotFound() {
        // given
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, now);
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> readStatusService.create(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("읽음 상태 생성 실패 - 채널 없음")
    void create_fail_channelNotFound() {
        // given
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, now);
        User user = new User("user1", "user1@test.com", "password1", null);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> readStatusService.create(request))
                .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("읽음 상태 단건 조회 성공")
    void find_success() {
        // given
        User user = new User("user1", "user1@test.com", "password1", null);
        Channel channel = new Channel(ChannelType.PUBLIC, "channel1", null);
        ReadStatus readStatus = new ReadStatus(user, channel, now);
        ReadStatusDto expectedDto = new ReadStatusDto(readStatusId, userId, channelId, now);

        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));
        given(readStatusMapper.toDto(readStatus)).willReturn(expectedDto);

        // when
        ReadStatusDto result = readStatusService.find(readStatusId);

        // then
        assertThat(result.id()).isEqualTo(readStatusId);
    }

    @Test
    @DisplayName("읽음 상태 단건 조회 실패 - 없음")
    void find_fail_notFound() {
        // given
        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> readStatusService.find(readStatusId))
                .isInstanceOf(ReadStatusNotFoundException.class);
    }

    @Test
    @DisplayName("사용자별 읽음 상태 목록 조회")
    void findAllByUserId_success() {
        // given
        User user = new User("user1", "user1@test.com", "password1", null);
        Channel channel = new Channel(ChannelType.PUBLIC, "channel1", null);
        ReadStatus readStatus = new ReadStatus(user, channel, now);
        ReadStatusDto dto = new ReadStatusDto(readStatusId, userId, channelId, now);

        given(readStatusRepository.findAllByUser_Id(userId)).willReturn(List.of(readStatus));
        given(readStatusMapper.toDto(readStatus)).willReturn(dto);

        // when
        List<ReadStatusDto> result = readStatusService.findAllByUserId(userId);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("읽음 상태 수정 성공")
    void update_success() {
        // given
        Instant newLastReadAt = Instant.now();
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(newLastReadAt);
        User user = new User("user1", "user1@test.com", "password1", null);
        Channel channel = new Channel(ChannelType.PUBLIC, "channel1", null);
        ReadStatus readStatus = new ReadStatus(user, channel, now);
        ReadStatusDto expectedDto = new ReadStatusDto(readStatusId, userId, channelId, newLastReadAt);

        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));
        given(readStatusMapper.toDto(readStatus)).willReturn(expectedDto);

        // when
        ReadStatusDto result = readStatusService.update(readStatusId, request);

        // then
        assertThat(result.lastReadAt()).isEqualTo(newLastReadAt);
    }

    @Test
    @DisplayName("읽음 상태 수정 실패 - 없음")
    void update_fail_notFound() {
        // given
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(Instant.now());
        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> readStatusService.update(readStatusId, request))
                .isInstanceOf(ReadStatusNotFoundException.class);
    }

    @Test
    @DisplayName("읽음 상태 삭제 성공")
    void delete_success() {
        // given
        given(readStatusRepository.existsById(readStatusId)).willReturn(true);

        // when
        readStatusService.delete(readStatusId);

        // then
        then(readStatusRepository).should().deleteById(readStatusId);
    }

    @Test
    @DisplayName("읽음 상태 삭제 실패 - 없음")
    void delete_fail_notFound() {
        // given
        given(readStatusRepository.existsById(readStatusId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> readStatusService.delete(readStatusId))
                .isInstanceOf(ReadStatusNotFoundException.class);
    }
}
