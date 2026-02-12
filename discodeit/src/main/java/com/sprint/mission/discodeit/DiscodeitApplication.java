package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.manager.ChatManager;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
            .withZone(ZoneId.systemDefault());

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);
        AuthService authService = context.getBean(AuthService.class);
        ChatManager chatManager = context.getBean(ChatManager.class);
        UserStatusRepository statusRepo = context.getBean(UserStatusRepository.class);

        System.out.println("\n" + "=".repeat(95));
        System.out.println(" ".repeat(30) + "Discodeit 시스템 검증");
        System.out.println("=".repeat(95));

        // [1] 데이터 인프라 구축
        System.out.println("\n[1] 인프라 생성: 멤버 유저 및 채널(공개/비공개) 구축");
        UserDto.Response user1 = userService.create(new UserDto.CreateRequest("백지헌", "jiheon@fromis.com", "pw123", "010-2003-0417", null)).orElseThrow();
        UserDto.Response user2 = userService.create(new UserDto.CreateRequest("박지원", "jiwon@fromis.com", "pw456", "010-1998-0320", null)).orElseThrow();

        ChannelDto.Response publicChan1 = channelService.createPublicChannel(new ChannelDto.CreatePublicRequest("StayThisWay", "Summer Special Single", ChannelType.TEXT)).orElseThrow();
        ChannelDto.Response publicChan2 = channelService.createPublicChannel(new ChannelDto.CreatePublicRequest("LikeYouBetter", "2024 Single Album", ChannelType.TEXT)).orElseThrow();
        ChannelDto.Response privateChan = channelService.createPrivateChannel(new ChannelDto.CreatePrivateRequest("비밀채팅", "지헌과 지원의 비밀 대화", ChannelType.TEXT, List.of(user1.id(), user2.id()))).orElseThrow();

        System.out.println("\n[2] 보안 인증: AuthService 예외 처리 및 로그인 세션 검증");
        System.out.print("[Login Attempt 1] 백지헌 (Wrong Password): ");
        try {
            authService.login(new AuthDto.LoginRequest("jiheon@fromis.com", "wrong_pw"));
        } catch (Exception e) {
            System.out.println("인증 실패 (사유: " + e.getMessage() + ")");
        }

        System.out.print("[Login Attempt 2] 백지헌 (Correct Password): ");
        try {
            UserDto.Response loginUser = authService.login(new AuthDto.LoginRequest("jiheon@fromis.com", "pw123"));
            System.out.println("인증 성공! 접속자: " + loginUser.displayName() + " / 상태: " + (loginUser.isOnline() ? "🟢 ONLINE" : "🔴 OFFLINE"));
        } catch (Exception e) {
            System.err.println("에러: " + e.getMessage());
        }

        // [메시지 전송]
        chatManager.sendMessage(user1.id(), publicChan1.id(), "매일이 난 Sunday 월요일은 사라져");
        chatManager.sendMessage(user2.id(), publicChan1.id(), "Stay this way 깊고 짙은 Blue");

        System.out.println("\n[초기 시스템 스냅샷: 전원 ONLINE]");
        // 지헌(user1) 시점으로 시스템 확인
        printSystemSnapshot(userService, channelService, user1.id());
        printMessageHistory(publicChan1.id(), messageService, chatManager);

        // [3] 상태 시뮬레이션
        System.out.println("\n[3] 상태 시뮬레이션: '백지헌' 활동 기록을 10분 전으로 수정 (OFFLINE 확인용)");
        statusRepo.findByUserId(user1.id()).ifPresent(status -> {
            status.setLastAccessedAt(java.time.Instant.now().minus(10, java.time.temporal.ChronoUnit.MINUTES));
            statusRepo.save(status);
        });

        System.out.println("\n[중간 시스템 스냅샷: 지헌 OFFLINE 확인]");
        printSystemSnapshot(userService, channelService, user1.id());

        // [4] 데이터 무결성 테스트
        System.out.println("\n[4] 무결성 검증: '백지헌' 삭제 시 연쇄 삭제 및 비공개 채널 인원 갱신");
        userService.delete(user1.id());

        System.out.println("\n[최종 시스템 스냅샷: 삭제 후 정합성 확인]");
        printSystemSnapshot(userService, channelService, user2.id());
        printMessageHistory(publicChan1.id(), messageService, chatManager);

        // [5] 최종 보안 검증
        System.out.println("\n[5] 최종 보안 검증: 삭제된 계정 로그인 시도 ");
        try {
            authService.login(new AuthDto.LoginRequest("jiheon@fromis.com", "pw123"));
        } catch (Exception e) {
            System.out.println("로그인 차단 성공 (사유: 존재하지 않는 유저)");
        }

        System.out.println("\n" + "=".repeat(95));
        System.out.println(" ".repeat(38) + "테스트 종료");
        System.out.println("=".repeat(95));
    }

    private static void printSystemSnapshot(UserService us, ChannelService cs, UUID viewerId) {
        System.out.println("\n[USER STATUS TABLE]");
        System.out.printf("%-12s | %-22s | %-13s | %-8s\n", "Display Name", "Email", "Phone", "Status");
        System.out.println("-".repeat(75));
        us.findAll().forEach(u -> {
            String statusEmoji = u.isOnline() ? "🔵 ONLINE" : "🟠 OFFLINE";
            System.out.printf("%-12s | %-22s | %-13s | %-8s\n",
                    u.displayName(), u.email(), u.phoneNumber(), statusEmoji);
        });

        System.out.println("\n[CHANNEL INFO TABLE]");
        System.out.printf("%-18s | %-16s | %-12s | %-20s\n", "Name", "Type (Access)", "Participants", "Last Activity");
        System.out.println("-".repeat(85));

        // 과제 요구사항: 특정 유저 권한에 따른 채널 목록 필터링 조회
        cs.findAllByUserId(viewerId).forEach(c -> {
            String accessType = c.type() + (c.isPrivate() ? " (Private)" : " (Public)");
            String pStr = c.isPrivate() ? c.participantUserIds().size() + "명" : "전체";

            System.out.printf("%-18s | %-16s | %-12s | %-20s\n",
                    c.name(), accessType, pStr,
                    c.lastMessageAt() != null ? timeFormat.format(c.lastMessageAt()) : "(기록 없음)");
        });
    }

    private static void printMessageHistory(UUID channelId, MessageService ms, ChatManager cm) {
        System.out.println("\n[MESSAGE LOG]");
        List<MessageDto.Response> msgs = ms.findAllByChannelId(channelId);
        if (msgs.isEmpty()) {
            System.out.println("(출력할 메시지가 없습니다)");
        } else {
            for (MessageDto.Response m : msgs) {
                System.out.printf("[%s] %-10s : %s\n",
                        timeFormat.format(m.createdAt()), cm.getAuthorName(m.id()), m.content());
            }
        }
        System.out.println("------------------------------------------");
    }
}