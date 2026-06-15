package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
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
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
@SpringBootTest(properties = {
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true",
        "logging.level.org.hibernate.SQL=DEBUG"
})
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
  private BasicChannelService channelService;

  private UUID channelId;
  private UUID userId;
  private String channelName;
  private String channelDescription;
  private Channel channel;
  private ChannelDto channelDto;
  private User user;

  @BeforeEach
  void setUp() {
    channelId = UUID.randomUUID();
    userId = UUID.randomUUID();
    channelName = "testChannel";
    channelDescription = "testDescription";

    channel = new Channel(ChannelType.PUBLIC, channelName, channelDescription);
    ReflectionTestUtils.setField(channel, "id", channelId);
    channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, channelName, channelDescription,
        List.of(), Instant.now());
    user = new User("testUser", "test@example.com", "password", null);
  }

  @Test
  @DisplayName("공개 채널 생성 성공")
  void createPublicChannel_Success() {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest(channelName,
        channelDescription);
    given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

    // when
    ChannelDto result = channelService.create(request);

    // then
    assertThat(result).isEqualTo(channelDto);
    verify(channelRepository).save(any(Channel.class));
  }

  @Test
  @DisplayName("비공개 채널 생성 성공")
  void createPrivateChannel_Success() {
    // given
    List<UUID> participantIds = List.of(userId);
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
    given(userRepository.findAllById(eq(participantIds))).willReturn(List.of(user));
    given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

    // when
    ChannelDto result = channelService.create(request);

    // then
    assertThat(result).isEqualTo(channelDto);
    verify(channelRepository).save(any(Channel.class));
    verify(readStatusRepository).<ReadStatus>saveAll(anyList());
  }

  @Test
  @DisplayName("채널 조회 성공")
  void findChannel_Success() {
    // given
    given(channelRepository.findById(eq(channelId))).willReturn(Optional.of(channel));
    given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

    // when
    ChannelDto result = channelService.find(channelId);

    // then
    assertThat(result).isEqualTo(channelDto);
  }

  @Test
  @DisplayName("존재하지 않는 채널 조회 시 실패")
  void findChannel_WithNonExistentId_ThrowsException() {
    // given
    given(channelRepository.findById(eq(channelId))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> channelService.find(channelId))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("사용자별 채널 목록 조회 성공")
  void findAllByUserId_Success() {
    // given
    List<ReadStatus> readStatuses = List.of(new ReadStatus(user, channel, Instant.now(), true));
    given(readStatusRepository.findAllByUserId(eq(userId))).willReturn(readStatuses);
    given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), eq(List.of(channel.getId()))))
        .willReturn(List.of(channel));
    given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

    // when
    List<ChannelDto> result = channelService.findAllByUserId(userId);

    // then
    assertThat(result).containsExactly(channelDto);
  }

  @Test
  @DisplayName("공개 채널 수정 성공")
  void updatePublicChannel_Success() {
    // given
    String newName = "newChannelName";
    String newDescription = "newDescription";
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(newName, newDescription);

    given(channelRepository.findById(eq(channelId))).willReturn(Optional.of(channel));
    given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

    // when
    ChannelDto result = channelService.update(channelId, request);

    // then
    assertThat(result).isEqualTo(channelDto);
  }

  @Test
  @DisplayName("비공개 채널 수정 시도 시 실패")
  void updatePrivateChannel_ThrowsException() {
    // given
    Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newName",
        "newDescription");
    given(channelRepository.findById(eq(channelId))).willReturn(Optional.of(privateChannel));

    // when & then
    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(PrivateChannelUpdateException.class);
  }

  @Test
  @DisplayName("존재하지 않는 채널 수정 시도 시 실패")
  void updateChannel_WithNonExistentId_ThrowsException() {
    // given
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newName",
        "newDescription");
    given(channelRepository.findById(eq(channelId))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("채널 삭제 성공")
  void deleteChannel_Success() {
    // given
    given(channelRepository.existsById(eq(channelId))).willReturn(true);

    // when
    channelService.delete(channelId);

    // then
    verify(messageRepository).deleteAllByChannelId(eq(channelId));
    verify(readStatusRepository).deleteAllByChannelId(eq(channelId));
    verify(channelRepository).deleteById(eq(channelId));
  }

  @Test
  @DisplayName("존재하지 않는 채널 삭제 시도 시 실패")
  void deleteChannel_WithNonExistentId_ThrowsException() {
    // given
    given(channelRepository.existsById(eq(channelId))).willReturn(false);

    // when & then
    assertThatThrownBy(() -> channelService.delete(channelId))
        .isInstanceOf(ChannelNotFoundException.class);
  }
  @Test
  @DisplayName("캐시 적용 테스트: 두 번째 호출부터는 쿼리가 안 나가야 한다")
  void cacheTest() {
    // ⚠️ DB에 실제로 존재하는 유저의 UUID를 하나 넣어주세요!
    UUID testUserId = UUID.fromString("b2b40ffa-4630-491e-bf3a-1a489f06900a");

    System.out.println("========================================");
    System.out.println("🚀 [1차 호출] DB에서 데이터를 가져옵니다. (SELECT 쿼리 발생 O)");
    long start1 = System.currentTimeMillis();
    channelService.findAllByUserId(testUserId);
    long end1 = System.currentTimeMillis();
    System.out.println("⏱️ 1차 소요 시간: " + (end1 - start1) + "ms");

    System.out.println("\n🚀 [2차 호출] 캐시에서 데이터를 가져옵니다. (SELECT 쿼리 발생 X)");
    long start2 = System.currentTimeMillis();
    channelService.findAllByUserId(testUserId);
    long end2 = System.currentTimeMillis();
    System.out.println("⏱️ 2차 소요 시간: " + (end2 - start2) + "ms");
    System.out.println("========================================");
  }
} 