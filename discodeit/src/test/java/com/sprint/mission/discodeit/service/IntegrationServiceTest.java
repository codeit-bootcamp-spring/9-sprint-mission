package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.jcf.*;
import com.sprint.mission.discodeit.service.basic.*;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 요구사항 기반 통합 테스트
 *
 * 사용자 관리, 권한 관리, 채널 관리, 메시지 관리,
 * 메시지 수신 정보 관리, 바이너리 파일 다운로드 기능을 검증합니다.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationServiceTest {

    // Repositories
    private JCFUserRepository userRepository;
    private JCFUserStatusRepository userStatusRepository;
    private JCFBinaryContentRepository binaryContentRepository;
    private JCFChannelRepository channelRepository;
    private JCFMessageRepository messageRepository;
    private JCFReadStatusRepository readStatusRepository;

    // Services
    private UserService userService;
    private AuthService authService;
    private UserStatusService userStatusService;
    private ChannelService channelService;
    private MessageService messageService;
    private ReadStatusService readStatusService;
    private BinaryContentService binaryContentService;

    @BeforeEach
    void setUp() {
        // JCF Repository 초기화
        userRepository = new JCFUserRepository();
        userStatusRepository = new JCFUserStatusRepository();
        binaryContentRepository = new JCFBinaryContentRepository();
        channelRepository = new JCFChannelRepository();
        messageRepository = new JCFMessageRepository();
        readStatusRepository = new JCFReadStatusRepository();

        // Service 초기화
        userService = new BasicUserService(userRepository, binaryContentRepository, userStatusRepository);
        authService = new BasicAuthService(userRepository, userStatusRepository);
        userStatusService = new BasicUserStatusService(userStatusRepository, userRepository);
        channelService = new BasicChannelService(channelRepository, readStatusRepository, messageRepository, binaryContentRepository);
        messageService = new BasicMessageService(messageRepository, binaryContentRepository);
        readStatusService = new BasicReadStatusService(readStatusRepository, userRepository, channelRepository);
        binaryContentService = new BasicBinaryContentService(binaryContentRepository);
    }

    // ==================== 사용자 관리 테스트 ====================

    @Test
    @Order(1)
    @DisplayName("사용자를 등록할 수 있다")
    void createUser() {
        // given
        UserCreateRequest request = new UserCreateRequest("testuser", "test@example.com", "password123");

        // when
        UserResponse response = userService.create(request, null);

        // then
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals("testuser", response.username());
        assertEquals("test@example.com", response.email());
    }

    @Test
    @Order(2)
    @DisplayName("사용자 정보를 수정할 수 있다")
    void updateUser() {
        // given
        UserCreateRequest createRequest = new UserCreateRequest("olduser", "old@example.com", "oldpass");
        UserResponse createdUser = userService.create(createRequest, null);

        UserUpdateRequest updateRequest = new UserUpdateRequest("newuser", "new@example.com", "newpass");

        // when
        UserResponse updatedUser = userService.update(createdUser.id(), updateRequest, null);

        // then
        assertEquals(createdUser.id(), updatedUser.id());
        assertEquals("newuser", updatedUser.username());
        assertEquals("new@example.com", updatedUser.email());
    }

    @Test
    @Order(3)
    @DisplayName("사용자를 삭제할 수 있다")
    void deleteUser() {
        // given
        UserCreateRequest request = new UserCreateRequest("deleteuser", "delete@example.com", "password");
        UserResponse createdUser = userService.create(request, null);

        // when
        userService.delete(createdUser.id());

        // then
        List<UserDto> allUsers = userService.findAllAsDto();
        assertTrue(allUsers.stream().noneMatch(u -> u.id().equals(createdUser.id())));
    }

    @Test
    @Order(4)
    @DisplayName("모든 사용자를 조회할 수 있다")
    void findAllUsers() {
        // given
        userService.create(new UserCreateRequest("user1", "user1@example.com", "pass1"), null);
        userService.create(new UserCreateRequest("user2", "user2@example.com", "pass2"), null);
        userService.create(new UserCreateRequest("user3", "user3@example.com", "pass3"), null);

        // when
        List<UserDto> allUsers = userService.findAllAsDto();

        // then
        assertEquals(3, allUsers.size());
    }

    @Test
    @Order(5)
    @DisplayName("사용자의 온라인 상태를 업데이트할 수 있다")
    void updateUserOnlineStatus() {
        // given
        UserCreateRequest createRequest = new UserCreateRequest("statususer", "status@example.com", "password");
        UserResponse createdUser = userService.create(createRequest, null);

        UserStatus userStatus = userStatusService.findByUserId(createdUser.id());
        Instant newAccessTime = Instant.now();

        // when
        UserStatus updatedStatus = userStatusService.update(userStatus.getId(), new UserStatusUpdateRequest(newAccessTime));

        // then
        assertNotNull(updatedStatus);
        assertEquals(newAccessTime, updatedStatus.getLastActiveAt());
    }

    // ==================== 권한 관리 테스트 ====================

    @Test
    @Order(6)
    @DisplayName("사용자는 로그인할 수 있다")
    void loginUser() {
        // given
        UserCreateRequest createRequest = new UserCreateRequest("loginuser", "login@example.com", "mypassword");
        userService.create(createRequest, null);

        LoginRequest loginRequest = new LoginRequest("loginuser", "mypassword");

        // when
        UserResponse loggedInUser = authService.login(loginRequest);

        // then
        assertNotNull(loggedInUser);
        assertEquals("loginuser", loggedInUser.username());
        assertEquals("login@example.com", loggedInUser.email());
    }

    @Test
    @Order(7)
    @DisplayName("잘못된 비밀번호로 로그인 시 예외가 발생한다")
    void loginWithWrongPassword() {
        // given
        UserCreateRequest createRequest = new UserCreateRequest("wrongpassuser", "wrongpass@example.com", "correctpass");
        userService.create(createRequest, null);

        LoginRequest loginRequest = new LoginRequest("wrongpassuser", "wrongpassword");

        // when & then
        assertThrows(Exception.class, () -> authService.login(loginRequest));
    }

    // ==================== 채널 관리 테스트 ====================

    @Test
    @Order(8)
    @DisplayName("공개 채널을 생성할 수 있다")
    void createPublicChannel() {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("general", "일반 채널입니다");

        // when
        Channel response = channelService.createPublic(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(ChannelType.PUBLIC, response.getType());
        assertEquals("general", response.getName());
        assertEquals("일반 채널입니다", response.getDescription());
    }

    @Test
    @Order(9)
    @DisplayName("비공개 채널을 생성할 수 있다")
    void createPrivateChannel() {
        // given
        UserResponse user1 = userService.create(new UserCreateRequest("privateuser1", "private1@example.com", "pass"), null);
        UserResponse user2 = userService.create(new UserCreateRequest("privateuser2", "private2@example.com", "pass"), null);

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(user1.id(), user2.id()));

        // when
        Channel response = channelService.createPrivate(request);

        // then
        assertNotNull(response);
        assertEquals(ChannelType.PRIVATE, response.getType());
        assertNull(response.getName());
        assertNull(response.getDescription());
    }

    @Test
    @Order(10)
    @DisplayName("공개 채널의 정보를 수정할 수 있다")
    void updatePublicChannel() {
        // given
        PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest("oldchannel", "old description");
        Channel createdChannel = channelService.createPublic(createRequest);

        PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest("newchannel", "new description");

        // when
        Channel updatedChannel = channelService.update(createdChannel.getId(), updateRequest);

        // then
        assertEquals(createdChannel.getId(), updatedChannel.getId());
        assertEquals("newchannel", updatedChannel.getName());
        assertEquals("new description", updatedChannel.getDescription());
    }

    @Test
    @Order(11)
    @DisplayName("비공개 채널은 수정할 수 없다")
    void cannotUpdatePrivateChannel() {
        // given
        UserResponse user = userService.create(new UserCreateRequest("notupdateuser", "notupdate@example.com", "pass"), null);
        PrivateChannelCreateRequest createRequest = new PrivateChannelCreateRequest(List.of(user.id()));
        Channel privateChannel = channelService.createPrivate(createRequest);

        PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest("tryupdate", "try description");

        // when & then
        assertThrows(IllegalArgumentException.class, () -> channelService.update(privateChannel.getId(), updateRequest));
    }

    @Test
    @Order(12)
    @DisplayName("채널을 삭제할 수 있다")
    void deleteChannel() {
        // given
        PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest("deletechannel", "to be deleted");
        Channel createdChannel = channelService.createPublic(createRequest);

        // when
        channelService.delete(createdChannel.getId());

        // then
        UserResponse user = userService.create(new UserCreateRequest("deletechanneluser", "deletechannel@example.com", "pass"), null);
        List<ChannelDto> channels = channelService.findAllByUserId(user.id());
        assertTrue(channels.stream().noneMatch(c -> c.id().equals(createdChannel.getId())));
    }

    @Test
    @Order(13)
    @DisplayName("특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다")
    void findAllChannelsByUserId() {
        // given
        UserResponse user1 = userService.create(new UserCreateRequest("channeluser1", "channeluser1@example.com", "pass"), null);
        UserResponse user2 = userService.create(new UserCreateRequest("channeluser2", "channeluser2@example.com", "pass"), null);

        // 공개 채널 생성
        channelService.createPublic(new PublicChannelCreateRequest("public1", "공개채널1"));
        channelService.createPublic(new PublicChannelCreateRequest("public2", "공개채널2"));

        // 비공개 채널 생성 (user1만 참여)
        channelService.createPrivate(new PrivateChannelCreateRequest(List.of(user1.id())));

        // when
        List<ChannelDto> user1Channels = channelService.findAllByUserId(user1.id());
        List<ChannelDto> user2Channels = channelService.findAllByUserId(user2.id());

        // then
        assertEquals(3, user1Channels.size()); // 공개 2개 + 비공개 1개
        assertEquals(2, user2Channels.size()); // 공개 2개만
    }

    // ==================== 메시지 관리 테스트 ====================

    @Test
    @Order(14)
    @DisplayName("메시지를 보낼 수 있다")
    void createMessage() {
        // given
        UserResponse user = userService.create(new UserCreateRequest("msguser", "msguser@example.com", "pass"), null);
        Channel channel = channelService.createPublic(new PublicChannelCreateRequest("msgchannel", "메시지 채널"));

        MessageCreateRequest request = new MessageCreateRequest("안녕하세요!", channel.getId(), user.id());

        // when
        Message response = messageService.create(request, null);

        // then
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("안녕하세요!", response.getContent());
        assertEquals(channel.getId(), response.getChannelId());
        assertEquals(user.id(), response.getAuthorId());
    }

    @Test
    @Order(15)
    @DisplayName("메시지를 수정할 수 있다")
    void updateMessage() {
        // given
        UserResponse user = userService.create(new UserCreateRequest("msgupdateuser", "msgupdate@example.com", "pass"), null);
        Channel channel = channelService.createPublic(new PublicChannelCreateRequest("msgupdatechannel", "수정 테스트"));

        MessageCreateRequest createRequest = new MessageCreateRequest("원래 메시지", channel.getId(), user.id());
        Message createdMessage = messageService.create(createRequest, null);

        MessageUpdateRequest updateRequest = new MessageUpdateRequest("수정된 메시지");

        // when
        Message updatedMessage = messageService.update(createdMessage.getId(), updateRequest);

        // then
        assertEquals(createdMessage.getId(), updatedMessage.getId());
        assertEquals("수정된 메시지", updatedMessage.getContent());
    }

    @Test
    @Order(16)
    @DisplayName("메시지를 삭제할 수 있다")
    void deleteMessage() {
        // given
        UserResponse user = userService.create(new UserCreateRequest("msgdeleteuser", "msgdelete@example.com", "pass"), null);
        Channel channel = channelService.createPublic(new PublicChannelCreateRequest("msgdeletechannel", "삭제 테스트"));

        MessageCreateRequest createRequest = new MessageCreateRequest("삭제될 메시지", channel.getId(), user.id());
        Message createdMessage = messageService.create(createRequest, null);

        // when
        messageService.delete(createdMessage.getId());

        // then
        List<Message> messages = messageService.findAllByChannelId(channel.getId());
        assertTrue(messages.stream().noneMatch(m -> m.getId().equals(createdMessage.getId())));
    }

    @Test
    @Order(17)
    @DisplayName("특정 채널의 메시지 목록을 조회할 수 있다")
    void findAllMessagesByChannelId() {
        // given
        UserResponse user = userService.create(new UserCreateRequest("msglistuser", "msglist@example.com", "pass"), null);
        Channel channel = channelService.createPublic(new PublicChannelCreateRequest("msglistchannel", "메시지 목록"));

        messageService.create(new MessageCreateRequest("메시지1", channel.getId(), user.id()), null);
        messageService.create(new MessageCreateRequest("메시지2", channel.getId(), user.id()), null);
        messageService.create(new MessageCreateRequest("메시지3", channel.getId(), user.id()), null);

        // when
        List<Message> messages = messageService.findAllByChannelId(channel.getId());

        // then
        assertEquals(3, messages.size());
    }

    // ==================== 메시지 수신 정보 관리 테스트 ====================

    @Test
    @Order(18)
    @DisplayName("특정 채널의 메시지 수신 정보를 생성할 수 있다")
    void createReadStatus() {
        // given
        UserResponse user = userService.create(new UserCreateRequest("readstatususer", "readstatus@example.com", "pass"), null);
        Channel channel = channelService.createPublic(new PublicChannelCreateRequest("readstatuschannel", "수신정보 테스트"));

        ReadStatusCreateRequest request = new ReadStatusCreateRequest(user.id(), channel.getId(), Instant.now());

        // when
        ReadStatus readStatus = readStatusService.create(request);

        // then
        assertNotNull(readStatus);
        assertNotNull(readStatus.getId());
        assertEquals(user.id(), readStatus.getUserId());
        assertEquals(channel.getId(), readStatus.getChannelId());
    }

    @Test
    @Order(19)
    @DisplayName("특정 채널의 메시지 수신 정보를 수정할 수 있다")
    void updateReadStatus() {
        // given
        UserResponse user = userService.create(new UserCreateRequest("readupdateuser", "readupdate@example.com", "pass"), null);
        Channel channel = channelService.createPublic(new PublicChannelCreateRequest("readupdatechannel", "수신정보 수정"));

        ReadStatusCreateRequest createRequest = new ReadStatusCreateRequest(user.id(), channel.getId(), Instant.now().minusSeconds(3600));
        ReadStatus createdReadStatus = readStatusService.create(createRequest);

        Instant newLastReadAt = Instant.now();
        ReadStatusUpdateRequest updateRequest = new ReadStatusUpdateRequest(newLastReadAt);

        // when
        ReadStatus updatedReadStatus = readStatusService.update(createdReadStatus.getId(), updateRequest);

        // then
        assertEquals(createdReadStatus.getId(), updatedReadStatus.getId());
        assertEquals(newLastReadAt, updatedReadStatus.getLastReadAt());
    }

    @Test
    @Order(20)
    @DisplayName("특정 사용자의 메시지 수신 정보를 조회할 수 있다")
    void findAllReadStatusByUserId() {
        // given
        UserResponse user = userService.create(new UserCreateRequest("readfinduser", "readfind@example.com", "pass"), null);
        Channel channel1 = channelService.createPublic(new PublicChannelCreateRequest("readfindchannel1", "채널1"));
        Channel channel2 = channelService.createPublic(new PublicChannelCreateRequest("readfindchannel2", "채널2"));

        readStatusService.create(new ReadStatusCreateRequest(user.id(), channel1.getId(), Instant.now()));
        readStatusService.create(new ReadStatusCreateRequest(user.id(), channel2.getId(), Instant.now()));

        // when
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(user.id());

        // then
        assertEquals(2, readStatuses.size());
        assertTrue(readStatuses.stream().allMatch(rs -> rs.getUserId().equals(user.id())));
    }

    // ==================== 바이너리 파일 다운로드 테스트 ====================

    @Test
    @Order(21)
    @DisplayName("바이너리 파일을 1개 조회할 수 있다")
    void findOneBinaryContent() {
        // given
        BinaryContentCreateRequest request = new BinaryContentCreateRequest(
                "test.png",
                "image/png",
                new byte[]{1, 2, 3, 4, 5}
        );
        BinaryContent createdContent = binaryContentService.create(request);

        // when
        BinaryContent foundContent = binaryContentService.find(createdContent.getId());

        // then
        assertNotNull(foundContent);
        assertEquals(createdContent.getId(), foundContent.getId());
        assertEquals("test.png", foundContent.getFileName());
        assertEquals("image/png", foundContent.getContentType());
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5}, foundContent.getBytes());
    }

    @Test
    @Order(22)
    @DisplayName("바이너리 파일을 여러 개 조회할 수 있다")
    void findMultipleBinaryContents() {
        // given
        BinaryContent content1 = binaryContentService.create(new BinaryContentCreateRequest("file1.txt", "text/plain", "내용1".getBytes()));
        BinaryContent content2 = binaryContentService.create(new BinaryContentCreateRequest("file2.txt", "text/plain", "내용2".getBytes()));
        BinaryContent content3 = binaryContentService.create(new BinaryContentCreateRequest("file3.txt", "text/plain", "내용3".getBytes()));

        List<UUID> ids = List.of(content1.getId(), content2.getId(), content3.getId());

        // when
        List<BinaryContent> contents = binaryContentService.findAllByIdIn(ids);

        // then
        assertEquals(3, contents.size());
    }

    // ==================== 추가 통합 테스트 ====================

    @Test
    @Order(23)
    @DisplayName("프로필 이미지와 함께 사용자를 등록할 수 있다")
    void createUserWithProfile() {
        // given
        UserCreateRequest userRequest = new UserCreateRequest("profileuser", "profile@example.com", "password");
        BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest(
                "profile.jpg",
                "image/jpeg",
                new byte[]{10, 20, 30}
        );

        // when
        UserResponse response = userService.create(userRequest, profileRequest);

        // then
        assertNotNull(response);
        assertNotNull(response.profileId());
        assertEquals("profileuser", response.username());
    }

    @Test
    @Order(24)
    @DisplayName("첨부파일과 함께 메시지를 보낼 수 있다")
    void createMessageWithAttachments() {
        // given
        UserResponse user = userService.create(new UserCreateRequest("attachuser", "attach@example.com", "pass"), null);
        Channel channel = channelService.createPublic(new PublicChannelCreateRequest("attachchannel", "첨부파일 테스트"));

        MessageCreateRequest messageRequest = new MessageCreateRequest("첨부파일 있는 메시지", channel.getId(), user.id());
        List<BinaryContentCreateRequest> attachments = List.of(
                new BinaryContentCreateRequest("attach1.pdf", "application/pdf", new byte[]{1, 2, 3}),
                new BinaryContentCreateRequest("attach2.png", "image/png", new byte[]{4, 5, 6})
        );

        // when
        Message response = messageService.create(messageRequest, attachments);

        // then
        assertNotNull(response);
        assertNotNull(response.getAttachmentIds());
        assertEquals(2, response.getAttachmentIds().size());
    }

    @Test
    @Order(25)
    @DisplayName("채널 삭제 시 관련 메시지도 함께 삭제된다")
    void deleteChannelWithMessages() {
        // given
        UserResponse user = userService.create(new UserCreateRequest("cascadeuser", "cascade@example.com", "pass"), null);
        Channel channel = channelService.createPublic(new PublicChannelCreateRequest("cascadechannel", "cascade test"));

        messageService.create(new MessageCreateRequest("메시지1", channel.getId(), user.id()), null);
        messageService.create(new MessageCreateRequest("메시지2", channel.getId(), user.id()), null);

        // when
        channelService.delete(channel.getId());

        // then
        List<Message> messages = messageService.findAllByChannelId(channel.getId());
        assertTrue(messages.isEmpty());
    }
}
