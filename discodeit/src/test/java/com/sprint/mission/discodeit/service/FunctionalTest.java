package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.jcf.*;
import com.sprint.mission.discodeit.service.basic.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 기능 테스트 - 콘솔 및 파일 출력
 *
 * 모든 요구사항 기능을 테스트하고 결과를 콘솔과 파일에 출력합니다.
 */
public class FunctionalTest {

    // Repositories
    private final JCFUserRepository userRepository;
    private final JCFUserStatusRepository userStatusRepository;
    private final JCFBinaryContentRepository binaryContentRepository;
    private final JCFChannelRepository channelRepository;
    private final JCFMessageRepository messageRepository;
    private final JCFReadStatusRepository readStatusRepository;

    // Services
    private final UserService userService;
    private final AuthService authService;
    private final UserStatusService userStatusService;
    private final ChannelService channelService;
    private final MessageService messageService;
    private final ReadStatusService readStatusService;
    private final BinaryContentService binaryContentService;

    // Output
    private final StringBuilder outputBuffer;
    private int testCount = 0;
    private int passCount = 0;
    private int failCount = 0;

    public FunctionalTest() {
        // Repository 초기화
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

        outputBuffer = new StringBuilder();
    }

    public static void main(String[] args) {
        FunctionalTest test = new FunctionalTest();
        test.runAllTests();
    }

    private void runAllTests() {
        printHeader("DISCODEIT 기능 테스트");
        printLine("테스트 시작 시간: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        printSeparator();

        // 사용자 관리 테스트
        printSection("1. 사용자 관리");
        testUserCreate();
        testUserUpdate();
        testUserDelete();
        testUserFindAll();
        testUserStatusUpdate();

        // 권한 관리 테스트
        printSection("2. 권한 관리");
        testLogin();
        testLoginFail();

        // 채널 관리 테스트
        printSection("3. 채널 관리");
        testPublicChannelCreate();
        testPrivateChannelCreate();
        testPublicChannelUpdate();
        testPrivateChannelUpdateFail();
        testChannelDelete();
        testFindChannelsByUserId();

        // 메시지 관리 테스트
        printSection("4. 메시지 관리");
        testMessageCreate();
        testMessageUpdate();
        testMessageDelete();
        testFindMessagesByChannelId();

        // 메시지 수신 정보 관리 테스트
        printSection("5. 메시지 수신 정보 관리");
        testReadStatusCreate();
        testReadStatusUpdate();
        testFindReadStatusByUserId();

        // 바이너리 파일 다운로드 테스트
        printSection("6. 바이너리 파일 다운로드");
        testBinaryContentFindOne();
        testBinaryContentFindMultiple();

        // 결과 요약
        printSummary();

        // 파일로 저장
        saveToFile();
    }

    // ==================== 사용자 관리 테스트 ====================

    private void testUserCreate() {
        String testName = "사용자를 등록할 수 있다";
        try {
            UserCreateRequest request = new UserCreateRequest("홍길동", "hong@example.com", "password123");
            UserResponse response = userService.create(request, null);

            printTestResult(testName, true);
            printDetail("생성된 사용자 ID: " + response.id());
            printDetail("사용자명: " + response.username());
            printDetail("이메일: " + response.email());
            printDetail("생성일시: " + response.createdAt());
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    private void testUserUpdate() {
        String testName = "사용자 정보를 수정할 수 있다";
        try {
            // 먼저 사용자 생성
            UserCreateRequest createRequest = new UserCreateRequest("김철수", "kim@example.com", "pass123");
            UserResponse createdUser = userService.create(createRequest, null);

            // 사용자 정보 수정
            UserUpdateRequest updateRequest = new UserUpdateRequest("김영희", "younghee@example.com", "newpass456");
            UserResponse updatedUser = userService.update(createdUser.id(), updateRequest, null);

            printTestResult(testName, true);
            printDetail("수정 전 사용자명: 김철수 → 수정 후: " + updatedUser.username());
            printDetail("수정 전 이메일: kim@example.com → 수정 후: " + updatedUser.email());
            printDetail("수정일시: " + updatedUser.updatedAt());
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    private void testUserDelete() {
        String testName = "사용자를 삭제할 수 있다";
        try {
            // 삭제할 사용자 생성
            UserCreateRequest request = new UserCreateRequest("삭제될사용자", "delete@example.com", "pass");
            UserResponse createdUser = userService.create(request, null);
            UUID userId = createdUser.id();

            // 삭제 전 사용자 수
            int beforeCount = userService.findAllAsDto().size();

            // 사용자 삭제
            userService.delete(userId);

            // 삭제 후 사용자 수
            int afterCount = userService.findAllAsDto().size();

            printTestResult(testName, true);
            printDetail("삭제된 사용자 ID: " + userId);
            printDetail("삭제 전 사용자 수: " + beforeCount + " → 삭제 후: " + afterCount);
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    private void testUserFindAll() {
        String testName = "모든 사용자를 조회할 수 있다";
        try {
            // 추가 사용자 생성
            userService.create(new UserCreateRequest("사용자A", "userA@example.com", "pass"), null);
            userService.create(new UserCreateRequest("사용자B", "userB@example.com", "pass"), null);

            List<UserDto> allUsers = userService.findAllAsDto();

            printTestResult(testName, true);
            printDetail("전체 사용자 수: " + allUsers.size());
            for (UserDto user : allUsers) {
                printDetail("  - " + user.username() + " (" + user.email() + ") - 온라인: " + user.online());
            }
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    private void testUserStatusUpdate() {
        String testName = "사용자의 온라인 상태를 업데이트할 수 있다";
        try {
            // 사용자 생성
            UserCreateRequest request = new UserCreateRequest("상태테스트", "status@example.com", "pass");
            UserResponse user = userService.create(request, null);

            // UserStatus 조회
            UserStatus userStatus = userStatusService.findByUserId(user.id());
            Instant beforeTime = userStatus.getLastActiveAt();

            // 상태 업데이트
            Thread.sleep(100); // 시간 차이를 위해
            Instant newTime = Instant.now();
            UserStatus updatedStatus = userStatusService.update(userStatus.getId(), new UserStatusUpdateRequest(newTime));

            printTestResult(testName, true);
            printDetail("사용자 ID: " + user.id());
            printDetail("업데이트 전 마지막 활동: " + beforeTime);
            printDetail("업데이트 후 마지막 활동: " + updatedStatus.getLastActiveAt());
            printDetail("온라인 상태: " + updatedStatus.isOnline());
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    // ==================== 권한 관리 테스트 ====================

    private void testLogin() {
        String testName = "사용자는 로그인할 수 있다";
        try {
            // 로그인할 사용자 생성
            UserCreateRequest createRequest = new UserCreateRequest("로그인사용자", "login@example.com", "mypassword");
            userService.create(createRequest, null);

            // 로그인 시도
            LoginRequest loginRequest = new LoginRequest("로그인사용자", "mypassword");
            UserResponse loggedInUser = authService.login(loginRequest);

            printTestResult(testName, true);
            printDetail("로그인 사용자: " + loggedInUser.username());
            printDetail("이메일: " + loggedInUser.email());
            printDetail("사용자 ID: " + loggedInUser.id());
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    private void testLoginFail() {
        String testName = "잘못된 비밀번호로 로그인 시 실패한다";
        try {
            // 사용자 생성
            UserCreateRequest createRequest = new UserCreateRequest("실패테스트", "fail@example.com", "correctpass");
            userService.create(createRequest, null);

            // 잘못된 비밀번호로 로그인 시도
            LoginRequest loginRequest = new LoginRequest("실패테스트", "wrongpassword");
            try {
                authService.login(loginRequest);
                printTestResult(testName, false);
                printDetail("예외가 발생해야 하는데 발생하지 않음");
            } catch (Exception e) {
                printTestResult(testName, true);
                printDetail("예상대로 로그인 실패: " + e.getMessage());
            }
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    // ==================== 채널 관리 테스트 ====================

    private void testPublicChannelCreate() {
        String testName = "공개 채널을 생성할 수 있다";
        try {
            PublicChannelCreateRequest request = new PublicChannelCreateRequest("일반", "일반 대화 채널입니다");
            Channel response = channelService.createPublic(request);

            printTestResult(testName, true);
            printDetail("채널 ID: " + response.getId());
            printDetail("채널 타입: " + response.getType());
            printDetail("채널명: " + response.getName());
            printDetail("설명: " + response.getDescription());
            printDetail("생성일시: " + response.getCreatedAt());
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    private void testPrivateChannelCreate() {
        String testName = "비공개 채널을 생성할 수 있다";
        try {
            // 참여자 생성
            UserResponse user1 = userService.create(new UserCreateRequest("비공개1", "private1@example.com", "pass"), null);
            UserResponse user2 = userService.create(new UserCreateRequest("비공개2", "private2@example.com", "pass"), null);

            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(user1.id(), user2.id()));
            Channel response = channelService.createPrivate(request);

            printTestResult(testName, true);
            printDetail("채널 ID: " + response.getId());
            printDetail("채널 타입: " + response.getType());
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    private void testPublicChannelUpdate() {
        String testName = "공개 채널의 정보를 수정할 수 있다";
        try {
            // 채널 생성
            PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest("수정전채널", "수정 전 설명");
            Channel createdChannel = channelService.createPublic(createRequest);

            // 채널 수정
            PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest("수정후채널", "수정 후 설명입니다");
            Channel updatedChannel = channelService.update(createdChannel.getId(), updateRequest);

            printTestResult(testName, true);
            printDetail("수정 전 채널명: 수정전채널 → 수정 후: " + updatedChannel.getName());
            printDetail("수정 전 설명: 수정 전 설명 → 수정 후: " + updatedChannel.getDescription());
            printDetail("수정일시: " + updatedChannel.getUpdatedAt());
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    private void testPrivateChannelUpdateFail() {
        String testName = "비공개 채널은 수정할 수 없다";
        try {
            // 비공개 채널 생성
            UserResponse user = userService.create(new UserCreateRequest("수정불가", "noupdate@example.com", "pass"), null);
            PrivateChannelCreateRequest createRequest = new PrivateChannelCreateRequest(List.of(user.id()));
            Channel privateChannel = channelService.createPrivate(createRequest);

            // 수정 시도
            PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest("수정시도", "수정 시도 설명");
            try {
                channelService.update(privateChannel.getId(), updateRequest);
                printTestResult(testName, false);
                printDetail("예외가 발생해야 하는데 발생하지 않음");
            } catch (IllegalArgumentException e) {
                printTestResult(testName, true);
                printDetail("예상대로 수정 실패: " + e.getMessage());
            }
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    private void testChannelDelete() {
        String testName = "채널을 삭제할 수 있다";
        try {
            // 채널 생성
            PublicChannelCreateRequest request = new PublicChannelCreateRequest("삭제될채널", "곧 삭제됩니다");
            Channel createdChannel = channelService.createPublic(request);
            UUID channelId = createdChannel.getId();

            // 채널 삭제
            channelService.delete(channelId);

            printTestResult(testName, true);
            printDetail("삭제된 채널 ID: " + channelId);
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    private void testFindChannelsByUserId() {
        String testName = "특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다";
        try {
            // 사용자 생성
            UserResponse user1 = userService.create(new UserCreateRequest("채널조회1", "channel1@example.com", "pass"), null);
            UserResponse user2 = userService.create(new UserCreateRequest("채널조회2", "channel2@example.com", "pass"), null);

            // 공개 채널 생성
            channelService.createPublic(new PublicChannelCreateRequest("공개채널A", "공개A"));
            channelService.createPublic(new PublicChannelCreateRequest("공개채널B", "공개B"));

            // 비공개 채널 생성 (user1만 참여)
            channelService.createPrivate(new PrivateChannelCreateRequest(List.of(user1.id())));

            // 각 사용자별 채널 조회
            List<ChannelDto> user1Channels = channelService.findAllByUserId(user1.id());
            List<ChannelDto> user2Channels = channelService.findAllByUserId(user2.id());

            printTestResult(testName, true);
            printDetail("사용자1 (비공개 채널 참여) 조회 가능 채널 수: " + user1Channels.size());
            for (ChannelDto ch : user1Channels) {
                printDetail("  - [" + ch.type() + "] " + (ch.name() != null ? ch.name() : "비공개채널"));
            }
            printDetail("사용자2 (비공개 채널 미참여) 조회 가능 채널 수: " + user2Channels.size());
            for (ChannelDto ch : user2Channels) {
                printDetail("  - [" + ch.type() + "] " + (ch.name() != null ? ch.name() : "비공개채널"));
            }
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    // ==================== 메시지 관리 테스트 ====================

    private void testMessageCreate() {
        String testName = "메시지를 보낼 수 있다";
        try {
            // 사용자와 채널 생성
            UserResponse user = userService.create(new UserCreateRequest("메시지작성자", "msg@example.com", "pass"), null);
            Channel channel = channelService.createPublic(new PublicChannelCreateRequest("메시지채널", "메시지 테스트"));

            // 메시지 생성
            MessageCreateRequest request = new MessageCreateRequest("안녕하세요! 첫 번째 메시지입니다.", channel.getId(), user.id());
            Message response = messageService.create(request, null);

            printTestResult(testName, true);
            printDetail("메시지 ID: " + response.getId());
            printDetail("내용: " + response.getContent());
            printDetail("작성자 ID: " + response.getAuthorId());
            printDetail("채널 ID: " + response.getChannelId());
            printDetail("작성일시: " + response.getCreatedAt());
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    private void testMessageUpdate() {
        String testName = "메시지를 수정할 수 있다";
        try {
            // 사용자와 채널 생성
            UserResponse user = userService.create(new UserCreateRequest("메시지수정자", "msgupdate@example.com", "pass"), null);
            Channel channel = channelService.createPublic(new PublicChannelCreateRequest("수정테스트채널", "수정 테스트"));

            // 메시지 생성
            MessageCreateRequest createRequest = new MessageCreateRequest("원래 메시지 내용", channel.getId(), user.id());
            Message createdMessage = messageService.create(createRequest, null);

            // 메시지 수정
            MessageUpdateRequest updateRequest = new MessageUpdateRequest("수정된 메시지 내용입니다!");
            Message updatedMessage = messageService.update(createdMessage.getId(), updateRequest);

            printTestResult(testName, true);
            printDetail("수정 전: 원래 메시지 내용");
            printDetail("수정 후: " + updatedMessage.getContent());
            printDetail("수정일시: " + updatedMessage.getUpdatedAt());
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    private void testMessageDelete() {
        String testName = "메시지를 삭제할 수 있다";
        try {
            // 사용자와 채널 생성
            UserResponse user = userService.create(new UserCreateRequest("메시지삭제자", "msgdelete@example.com", "pass"), null);
            Channel channel = channelService.createPublic(new PublicChannelCreateRequest("삭제테스트채널", "삭제 테스트"));

            // 메시지 생성
            MessageCreateRequest request = new MessageCreateRequest("삭제될 메시지", channel.getId(), user.id());
            Message createdMessage = messageService.create(request, null);
            UUID messageId = createdMessage.getId();

            // 삭제 전 메시지 수
            int beforeCount = messageService.findAllByChannelId(channel.getId()).size();

            // 메시지 삭제
            messageService.delete(messageId);

            // 삭제 후 메시지 수
            int afterCount = messageService.findAllByChannelId(channel.getId()).size();

            printTestResult(testName, true);
            printDetail("삭제된 메시지 ID: " + messageId);
            printDetail("삭제 전 메시지 수: " + beforeCount + " → 삭제 후: " + afterCount);
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    private void testFindMessagesByChannelId() {
        String testName = "특정 채널의 메시지 목록을 조회할 수 있다";
        try {
            // 사용자와 채널 생성
            UserResponse user = userService.create(new UserCreateRequest("메시지목록", "msglist@example.com", "pass"), null);
            Channel channel = channelService.createPublic(new PublicChannelCreateRequest("목록테스트채널", "목록 테스트"));

            // 여러 메시지 생성
            messageService.create(new MessageCreateRequest("첫 번째 메시지", channel.getId(), user.id()), null);
            messageService.create(new MessageCreateRequest("두 번째 메시지", channel.getId(), user.id()), null);
            messageService.create(new MessageCreateRequest("세 번째 메시지", channel.getId(), user.id()), null);

            // 메시지 목록 조회
            List<Message> messages = messageService.findAllByChannelId(channel.getId());

            printTestResult(testName, true);
            printDetail("채널 ID: " + channel.getId());
            printDetail("메시지 수: " + messages.size());
            for (Message msg : messages) {
                printDetail("  - " + msg.getContent() + " (작성: " + msg.getCreatedAt() + ")");
            }
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    // ==================== 메시지 수신 정보 관리 테스트 ====================

    private void testReadStatusCreate() {
        String testName = "특정 채널의 메시지 수신 정보를 생성할 수 있다";
        try {
            // 사용자와 채널 생성
            UserResponse user = userService.create(new UserCreateRequest("수신정보생성", "readcreate@example.com", "pass"), null);
            Channel channel = channelService.createPublic(new PublicChannelCreateRequest("수신정보채널", "수신 정보 테스트"));

            // 수신 정보 생성
            Instant now = Instant.now();
            ReadStatusCreateRequest request = new ReadStatusCreateRequest(user.id(), channel.getId(), now);
            ReadStatus readStatus = readStatusService.create(request);

            printTestResult(testName, true);
            printDetail("수신 정보 ID: " + readStatus.getId());
            printDetail("사용자 ID: " + readStatus.getUserId());
            printDetail("채널 ID: " + readStatus.getChannelId());
            printDetail("마지막 읽은 시간: " + readStatus.getLastReadAt());
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    private void testReadStatusUpdate() {
        String testName = "특정 채널의 메시지 수신 정보를 수정할 수 있다";
        try {
            // 사용자와 채널 생성
            UserResponse user = userService.create(new UserCreateRequest("수신정보수정", "readupdate@example.com", "pass"), null);
            Channel channel = channelService.createPublic(new PublicChannelCreateRequest("수신수정채널", "수신 수정 테스트"));

            // 수신 정보 생성
            Instant oldTime = Instant.now().minusSeconds(3600);
            ReadStatusCreateRequest createRequest = new ReadStatusCreateRequest(user.id(), channel.getId(), oldTime);
            ReadStatus createdReadStatus = readStatusService.create(createRequest);

            // 수신 정보 수정
            Instant newTime = Instant.now();
            ReadStatusUpdateRequest updateRequest = new ReadStatusUpdateRequest(newTime);
            ReadStatus updatedReadStatus = readStatusService.update(createdReadStatus.getId(), updateRequest);

            printTestResult(testName, true);
            printDetail("수정 전 마지막 읽은 시간: " + oldTime);
            printDetail("수정 후 마지막 읽은 시간: " + updatedReadStatus.getLastReadAt());
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    private void testFindReadStatusByUserId() {
        String testName = "특정 사용자의 메시지 수신 정보를 조회할 수 있다";
        try {
            // 사용자와 채널 생성
            UserResponse user = userService.create(new UserCreateRequest("수신정보조회", "readfind@example.com", "pass"), null);
            Channel channel1 = channelService.createPublic(new PublicChannelCreateRequest("수신조회채널1", "채널1"));
            Channel channel2 = channelService.createPublic(new PublicChannelCreateRequest("수신조회채널2", "채널2"));

            // 수신 정보 생성
            readStatusService.create(new ReadStatusCreateRequest(user.id(), channel1.getId(), Instant.now()));
            readStatusService.create(new ReadStatusCreateRequest(user.id(), channel2.getId(), Instant.now()));

            // 수신 정보 조회
            List<ReadStatus> readStatuses = readStatusService.findAllByUserId(user.id());

            printTestResult(testName, true);
            printDetail("사용자 ID: " + user.id());
            printDetail("수신 정보 수: " + readStatuses.size());
            for (ReadStatus rs : readStatuses) {
                printDetail("  - 채널 ID: " + rs.getChannelId() + ", 마지막 읽은 시간: " + rs.getLastReadAt());
            }
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    // ==================== 바이너리 파일 다운로드 테스트 ====================

    private void testBinaryContentFindOne() {
        String testName = "바이너리 파일을 1개 조회할 수 있다";
        try {
            // 바이너리 콘텐츠 생성
            byte[] data = "테스트 파일 내용입니다.".getBytes();
            BinaryContentCreateRequest request = new BinaryContentCreateRequest("test.txt", "text/plain", data);
            BinaryContent createdContent = binaryContentService.create(request);

            // 바이너리 콘텐츠 조회
            BinaryContent foundContent = binaryContentService.find(createdContent.getId());

            printTestResult(testName, true);
            printDetail("파일 ID: " + foundContent.getId());
            printDetail("파일명: " + foundContent.getFileName());
            printDetail("Content-Type: " + foundContent.getContentType());
            printDetail("데이터 크기: " + foundContent.getBytes().length + " bytes");
            printDetail("데이터 내용: " + new String(foundContent.getBytes()));
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    private void testBinaryContentFindMultiple() {
        String testName = "바이너리 파일을 여러 개 조회할 수 있다";
        try {
            // 여러 바이너리 콘텐츠 생성
            BinaryContent content1 = binaryContentService.create(
                    new BinaryContentCreateRequest("file1.txt", "text/plain", "내용1".getBytes()));
            BinaryContent content2 = binaryContentService.create(
                    new BinaryContentCreateRequest("file2.png", "image/png", new byte[]{1, 2, 3, 4, 5}));
            BinaryContent content3 = binaryContentService.create(
                    new BinaryContentCreateRequest("file3.pdf", "application/pdf", new byte[]{10, 20, 30}));

            // 여러 개 조회
            List<UUID> ids = List.of(content1.getId(), content2.getId(), content3.getId());
            List<BinaryContent> contents = binaryContentService.findAllByIdIn(ids);

            printTestResult(testName, true);
            printDetail("요청한 파일 수: " + ids.size());
            printDetail("조회된 파일 수: " + contents.size());
            for (BinaryContent content : contents) {
                printDetail("  - " + content.getFileName() + " (" + content.getContentType() + ", " + content.getBytes().length + " bytes)");
            }
        } catch (Exception e) {
            printTestResult(testName, false);
            printDetail("오류: " + e.getMessage());
        }
    }

    // ==================== 출력 헬퍼 메서드 ====================

    private void print(String message) {
        System.out.println(message);
        outputBuffer.append(message).append("\n");
    }

    private void printHeader(String title) {
        String header = "\n" + "=".repeat(60) + "\n" + "  " + title + "\n" + "=".repeat(60);
        print(header);
    }

    private void printSection(String section) {
        print("\n" + "-".repeat(50));
        print("[ " + section + " ]");
        print("-".repeat(50));
    }

    private void printSeparator() {
        print("-".repeat(60));
    }

    private void printLine(String line) {
        print(line);
    }

    private void printTestResult(String testName, boolean passed) {
        testCount++;
        if (passed) {
            passCount++;
            print("\n[PASS] " + testName);
        } else {
            failCount++;
            print("\n[FAIL] " + testName);
        }
    }

    private void printDetail(String detail) {
        print("       " + detail);
    }

    private void printSummary() {
        print("\n" + "=".repeat(60));
        print("  테스트 결과 요약");
        print("=".repeat(60));
        print("전체 테스트: " + testCount);
        print("성공: " + passCount);
        print("실패: " + failCount);
        print("성공률: " + String.format("%.1f", (passCount * 100.0 / testCount)) + "%");
        print("=".repeat(60));
        print("테스트 종료 시간: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    private void saveToFile() {
        String fileName = "test_result_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
        String filePath = "build/" + fileName;

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.print(outputBuffer.toString());
            print("\n결과가 파일로 저장되었습니다: " + filePath);
        } catch (IOException e) {
            print("\n파일 저장 실패: " + e.getMessage());
        }
    }
}
