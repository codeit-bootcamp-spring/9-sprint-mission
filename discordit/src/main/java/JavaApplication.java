//import entity.Channel;
//import entity.Message;
//import entity.User;
//import service.ChannelService;
//import service.UserService;
//import service.MessageService;
//import service.jcf.JCFChannelService;
//import service.jcf.JCFUserService;
//import service.jcf.JCFMessageService;
//
//import java.util.List;
//import java.util.UUID;
//
//public class JavaApplication {
//    public static void maidn(String[] args) {
//        UserService userService = new JCFUserService();
//        ChannelService channelService = new JCFChannelService();
//        MessageService messageService = new JCFMessageService();
//
//        // ========================================
//        // 1. 사용자 추가 (CREATE)
//        // ========================================
//        System.out.println("════════════════════════════════════════");
//        System.out.println("          1. 사용자 추가 테스트");
//        System.out.println("════════════════════════════════════════\n");
//
//        User user1 = new User("최건위", "cdaqwe@naver.com", "010-1010-1011");
//        User user2 = new User("김한수", "chlrjsdlll@naver.com", "010-0100-1001");
//        User user3 = new User("홍길동", "hong@gmail.com", "010-2222-2222");
//
//        userService.addUser(user1);
//        System.out.println("✓ 김한수 추가 완료");
//
//        userService.addUser(user2);
//        System.out.println("✓ 최건위 추가 완료");
//
//        userService.addUser(user3);
//        System.out.println("✓ 홍길동 추가 완료");
//
//        System.out.println("\n→ 총 " + userService.getallUser().size() + "명 추가됨\n");
//
//        // ========================================
//        // 2. 사용자 조회 (READ)
//        // ========================================
//        System.out.println("════════════════════════════════════════");
//        System.out.println("          2. 사용자 조회 테스트");
//        System.out.println("════════════════════════════════════════\n");
//
//        User foundUser = userService.getUser("최건위");
//        if (foundUser == null) {
//            System.out.println("✗ 조회 실패: 사용자 없음");
//        } else {
//            System.out.println("✓ 조회 성공!");
//            System.out.println("  - ID: " +  foundUser.getById());
//            System.out.println("  - 이름: " + foundUser.getdisplayName());
//            System.out.println("  - 이메일: " + foundUser.getEmail());
//            System.out.println("  - 전화번호: " + foundUser.getPhoneNumber());
//        }
//        System.out.println();
//
//        // ========================================
//        // 3. 전체 사용자 목록 (READ ALL)
//        // ========================================
//        System.out.println("════════════════════════════════════════");
//        System.out.println("        3. 전체 사용자 목록 조회");
//        System.out.println("════════════════════════════════════════\n");
//
//        List<User> users = userService.getallUser();
//        System.out.println("총 " + users.size() + "명의 사용자:");
//        for (int i = 0; i < users.size(); i++) {
//            User u = users.get(i);
//            System.out.println((i + 1) + ". " + u.getdisplayName() +
//                    " (" + u.getEmail() + ")");
//        }
//        System.out.println();
//
//        // ========================================
//        // 4. 사용자 정보 수정 (UPDATE)
//        // ========================================
//        System.out.println("════════════════════════════════════════");
//        System.out.println("          4. 사용자 수정 테스트");
//        System.out.println("════════════════════════════════════════\n");
//
////업데이트 실패가 왜 나타나는지?? Id는 왜 안나타는지??
//        User beforeUpdate = userService.getUser("김한수");
//        if (beforeUpdate == null) {
//            System.out.println("✗ 수정 실패: 사용자를 찾을 수 없습니다.");
//            System.out.println();
//        } else {
//            System.out.println("【수정 전】");
//            System.out.println("  이름: " + beforeUpdate.getdisplayName());
//            System.out.println("  ID: " + beforeUpdate.getById());
//            System.out.println("  이메일: " + beforeUpdate.getEmail());
//            System.out.println("  전화번호: " + beforeUpdate.getPhoneNumber());
//
//            User updatedUser = userService.updateUser("김한수","홍길동",
//                    "choi@asdasd.com",
//                    "010-9999-1111");
//
//            // 수정 결과 확인
//            if (updatedUser != null) {
//                System.out.println("\n✓ 업데이트 완료!\n");
//
//                System.out.println("【수정 후】");
//                System.out.println("  이름: " + updatedUser.getdisplayName());
//                System.out.println("  ID: " + updatedUser.getById());
//                System.out.println("  이메일: " + updatedUser.getEmail());
//                System.out.println("  전화번호: " + updatedUser.getPhoneNumber());
//            } else {
//                System.out.println("\n✗ 업데이트 실패!");
//            }
//            System.out.println();
//        }
//
//
//        // ========================================
//        // 5. 사용자 삭제 (DELETE)
//        // ========================================
//        System.out.println("════════════════════════════════════════");
//        System.out.println("          5. 사용자 삭제 테스트");
//        System.out.println("════════════════════════════════════════\n");
//
//        System.out.println("삭제 대상: 홍길동");
//        boolean deleteResult = userService.deleteUser("홍길동");
//
//        if (deleteResult) {
//            System.out.println("✓ 삭제 성공!");
//        } else {
//            System.out.println("✗ 삭제 실패: 사용자를 찾을 수 없음");
//        }
//
//        // 삭제 확인
//        User deletedCheck = userService.getUser("홍길동");
//        System.out.println("삭제 확인: " + (deletedCheck == null ? "삭제됨 ✓" : "여전히 존재함 ✗"));
//        System.out.println();
//
//        // ========================================
//        // 6. 최종 사용자 목록
//        // ========================================
//        System.out.println("════════════════════════════════════════");
//        System.out.println("          6. 최종 사용자 목록");
//        System.out.println("════════════════════════════════════════\n");
//
//        List<User> finalUsers = userService.getallUser();
//        if (finalUsers.isEmpty()) {
//            System.out.println("(사용자 없음)");
//        } else {
//            System.out.println("총 " + finalUsers.size() + "명 남음:");
//            for (int i = 0; i < finalUsers.size(); i++) {
//                User u = finalUsers.get(i);
//                System.out.println((i + 1) + ". " + u.getdisplayName() +
//                        " (" + u.getEmail() + ", " +
//                        u.getPhoneNumber() + ")");
//            }
//        }
//
//        // ========================================
//        // 7. 채널 추가 (CREATE)
//        // ========================================
//        System.out.println("\n════════════════════════════════════════");
//        System.out.println("          7. 채널 추가 테스트");
//        System.out.println("════════════════════════════════════════\n");
//
//        Channel channel1 = new Channel("코드잇", "Java 백엔드 스터디", "USER_001");
//        Channel channel2 = new Channel("스프린터", "Java 백엔드 스터디", "USER_002");
//
//
//        channelService.addChannel(channel1);
//        System.out.println("✓ 코드잇 추가 완료");
//
//        channelService.addChannel(channel2);
//        System.out.println("✓ 스프린터 추가 완료");
//
//        System.out.println("\n→ 총 " + channelService.getallChannels().size() + "개 추가됨\n");
//
//
//        // ========================================
//        // 8.  채널 조회 (READ)
//        // ========================================
//        System.out.println("════════════════════════════════════════");
//        System.out.println("          8. 채널 조회 테스트");
//        System.out.println("════════════════════════════════════════\n");
//
//        // 4. 통합 검색 (이름 또는 소유자)
//        System.out.println("【통합 검색 - '스프린터'】");
//        List<Channel> search1 = channelService.searchChannels("스프린터");
//        System.out.println("검색 결과 " + search1.size() + "개:");
//        for (Channel ch : search1) {
//            System.out.println("  - " + ch.getChannelName() + ch.getDescription());
//        }
//
//        System.out.println("\n【통합 검색 - 'USER_001'】");
//        List<Channel> search2 = channelService.searchChannels("USER_001");
//        System.out.println("검색 결과 " + search2.size() + "개:");
//        for (Channel ch : search2) {
//            System.out.println("  - " + ch.getChannelName() + " " + ch.getDescription());
//        }
//
//        // ========================================
//        // 9. 전체 채널 목록 (READ ALL)
//        // ========================================
//        System.out.println("\n════════════════════════════════════════");
//        System.out.println("        9. 전체 채널 목록 조회");
//        System.out.println("════════════════════════════════════════\n");
//
//        List<Channel> channels = channelService.getallChannels();
//        System.out.println("총 " + channels.size() + "개의 채널:");
//        for (int i = 0; i < channels.size(); i++) {
//            Channel u = channels.get(i);
//            System.out.println((i + 1) + ". " + u.getChannelName() +
//                    " (" + u.getownerId() + " " + u.getDescription() + ")");
//        }
//        System.out.println();
//
//        // ========================================
//        // 10. 채널 수정 (UPDATE)
//        // ========================================
//        System.out.println("════════════════════════════════════════");
//        System.out.println("        10. 채널 수정");
//        System.out.println("════════════════════════════════════════\n");
//
//
//        Channel beforeChannelUpdate = channelService.getChannelByName("코드잇");
//        if (beforeChannelUpdate == null) {
//            System.out.println("✗ 수정 실패: 채널을 찾을 수 없습니다.");
//            System.out.println();
//        } else {
//            System.out.println("【수정 전】");
//            System.out.println("  채널명: " + beforeChannelUpdate.getChannelName());
//            System.out.println("  설명: " + beforeChannelUpdate.getDescription());
//            System.out.println("  소유자 ID: " + beforeChannelUpdate.getownerId());
//
//            Channel updatedChannel = channelService.updateChannel("코드잇", "코드잇v2",
//                    "업데이트된 Java 백엔드 학습방");
//
//            // 수정 결과 확인
//            if (updatedChannel != null) {
//                System.out.println("\n✓ 업데이트 완료!\n");
//
//                System.out.println("【수정 후】");
//                System.out.println("  채널명: " + updatedChannel.getChannelName());
//                System.out.println("  설명: " + updatedChannel.getDescription());
//                System.out.println("  소유자 ID: " + updatedChannel.getownerId());
//            } else {
//                System.out.println("\n✗ 업데이트 실패!");
//            }
//            System.out.println();
//        }
//        // ========================================
//        // 11. 채널 삭제 (DELETE)
//        // ========================================
//        System.out.println("════════════════════════════════════════");
//        System.out.println("        11. 채널 삭제");
//        System.out.println("════════════════════════════════════════\n");
//
//        System.out.println("삭제 대상: 코드잇");
//        boolean deleteChannel = channelService.deleteChannel("스프린터");
//
//        if (deleteResult) {
//            System.out.println("✓ 삭제 성공!");
//        } else {
//            System.out.println("✗ 삭제 실패: 채널을 찾을 수 없음");
//        }
//
//        System.out.println();
//
//        // ========================================
//        // 12. 최종 채널 목록
//        // ========================================
//        System.out.println("════════════════════════════════════════");
//        System.out.println("        12. 최종 채널 목록");
//        System.out.println("════════════════════════════════════════\n");
//
//
//        System.out.println("【최종 채널 목록】");
//        List<Channel> finalList = channelService.getallChannels();
//        System.out.println("총 " + finalList.size() + "개 남음:");
//        for (Channel ch : finalList) {
//            System.out.println("  - " + ch.getChannelName() + " (소유자: " + ch.getownerId() + " " + ch.getDescription() + ")");
//        }
//
//        // ========================================
//        // 13. 메시지 추가 테스트 (CREATE)
//        // ========================================
//        System.out.println("\n════════════════════════════════════════");
//        System.out.println("        13. 메시지 추가 테스트");
//        System.out.println("════════════════════════════════════════\n");
//
//        Message message1 = new Message("최건위", "행복한 코드잇", "User-001");
//        Message message2 = new Message("김한수", "오늘은 금요일이다.", "USER-002");
//
//
//        messageService.addMessage(message1);
//        System.out.println("✓ 메시지1 추가 완료");
//
//        messageService.addMessage(message2);
//        System.out.println("✓ 메시지2 추가 완료");
//
//
//        System.out.println("\n→ 총 " + userService.getallUser().size() + "명 추가됨\n");
//
//        // ========================================
//        // 14. 메시지 조회 (READ)
//        // ========================================
//        System.out.println("════════════════════════════════════════");
//        System.out.println("          14. 메시지 조회 테스트");
//        System.out.println("════════════════════════════════════════\n");
//
//        List<Message>foundMessages = messageService.getUsername("최건위");
//        if (foundMessages == null || foundMessages.isEmpty()) {
//            System.out.println("✗ 조회 실패: 메시지를 찾을 수 없습니다.");
//        } else {
//            System.out.println("✓ 조회 성공!");
//            System.out.println("최건위가 작성한 메시지 " + foundMessages.size() + "개:\n");
//
//            for (int i = 0; i < foundMessages.size(); i++) {
//                Message msg = foundMessages.get(i);
//                System.out.println((i + 1) + "번째 메시지:");
//                System.out.println("  - 이름: " + msg.getUsername());
//                System.out.println("  - 내용: " + msg.getContent());
//                System.out.println("  - CHANNEL ID: " + msg.getChannelId());
//            }
//        }
//
//        // ========================================
//        // 15. 전체 메시지 목록 (READ ALL)
//        // ========================================
//        System.out.println("════════════════════════════════════════");
//        System.out.println("        15. 전체 메시지 목록 조회");
//        System.out.println("════════════════════════════════════════\n");
//
//        List<Message> messages = messageService.getAllMessages();
//        System.out.println("총 " + messages.size() + "개의 메시지:");
//        for (int i = 0; i < messages.size(); i++) {
//            Message m = messages.get(i);
//            System.out.println((i + 1) + ". " + m.getUsername() +
//                    " (" + m.getContent() + " " + m.getChannelId() + ")");
//        }
//
//        // ========================================
//        // 16. 채널 수정 (UPDATE)
//        // ========================================
//        System.out.println("════════════════════════════════════════");
//        System.out.println("        16. 메시지 수정");
//        System.out.println("════════════════════════════════════════\n");
//
//
//        Message beforeMessageUpdate = messageService.getContent("행복한 코드잇");
//        if (beforeMessageUpdate == null) {
//            System.out.println("✗ 수정 실패: 메시지를 찾을 수 없습니다.");
//            System.out.println();
//        } else {
//            System.out.println("【수정 전】");
//            System.out.println("  내용: " + beforeMessageUpdate.getContent());
//            System.out.println("  이름: " + beforeMessageUpdate.getUsername());
//            System.out.println("  채널 ID: " + beforeMessageUpdate.getChannelId());
//
//            Message updatedMassage = messageService.updateMassage("행복한 코드잇","행복한 코드잇v2", "최건위",
//                    "USER-001");
//
//            // 수정 결과 확인
//            if (updatedMassage != null) {
//                System.out.println("\n✓ 업데이트 완료!\n");
//
//                System.out.println("【수정 후】");
//                System.out.println("  내용: " + updatedMassage.getContent());
//                System.out.println("  이름: " + updatedMassage.getUsername());
//                System.out.println("  채널 ID: " + updatedMassage.getChannelId());
//            } else {
//                System.out.println("\n✗ 업데이트 실패!");
//            }
//            System.out.println();
//        }
//
//        // ========================================
//        // 17. 메시지 삭제 (DELETE)
//        // ========================================
//        System.out.println("════════════════════════════════════════");
//        System.out.println("        17. 메시지 삭제");
//        System.out.println("════════════════════════════════════════\n");
//
//        System.out.println("삭제 메시지: 행복한 코드잇");
//        boolean deleteMessage = messageService.deleteMessage("행복한 코드잇");
//
//        if (deleteResult) {
//            System.out.println("✓ 삭제 성공!");
//        } else {
//            System.out.println("✗ 삭제 실패: 메시지를 찾을 수 없음");
//        }
//
//        System.out.println();
//
//        // ========================================
//        // 18. 최종 메시지 목록
//        // ========================================
//        System.out.println("════════════════════════════════════════");
//        System.out.println("        18. 최종 메시지 목록");
//        System.out.println("════════════════════════════════════════\n");
//
//
//        System.out.println("【최종 채널 목록】");
//        List<Message> finalms = messageService.getAllMessages();
//        System.out.println("총 " + finalList.size() + "개 남음:");
//        for (Message ms : finalms) {
//            System.out.println("  - " + ms.getUsername() + " (소유자: " + ms.getContent() + " " + ms.getChannelId() + ")");
//        }
//
//
//        // ========================================
//        // 19. 의존성 테스트
//        // ========================================
////        System.out.println("════════════════════════════════════════");
////        System.out.println("        19. 의존성 테스트");
////        System.out.println("════════════════════════════════════════\n");
////
////        Message message = messageService.createMessage(
////                User.getById(),      // ✅ UUID 그대로
////                Channel.getId(),   // ✅ UUID 그대로
////                "안녕하세요!"
////        );
////        System.out.println("✓ 메시지 생성 성공!");
////        System.out.println("  - 내용: " + message.getContent());
////
////// 에러 케이스
////        UUID fakeUserId = UUID.randomUUID();
////        try {
////            messageService.createMessage(fakeUserId, channel.getId(), "에러 메시지");
////        } catch (IllegalArgumentException e) {
////            System.out.println("✓ 예상된 에러: " + e.getMessage());
////        }
//
//        System.out.println("\n════════════════════════════════════════");
//        System.out.println("            테스트 완료!");
//        System.out.println("════════════════════════════════════════");
//    }
//}
import entity.Channel;
import entity.Message;
import entity.User;
import service.ChannelService;
import service.MessageService;
import service.UserService;
import service.file.FileChannelService;
import service.file.FileMessageService;
import service.file.FileUserService;

import java.util.List;

public class JavaApplication {

    static void userCRUDTest(UserService userService) {
        // 생성
        User user = new User("woody", "woody@codeit.com", "010-1234-5678");
        boolean created = userService.addUser(user);
        System.out.println("유저 생성 성공?: " + created);
        System.out.println("유저 생성: " + user.getId());

        // 조회(이름)
        User foundUser = userService.getUser("woody");
        System.out.println("유저 조회(이름): " + foundUser.getId());

        // 조회(UUID)
        User foundById = userService.getbyId(user.getId());
        System.out.println("유저 조회(UUID): " + foundById.getId());

        // 전체 조회
        List<User> foundUsers = userService.getallUser();
        System.out.println("유저 조회(다건): " + foundUsers.size());

        // 수정
        User updatedUser = userService.updateUser(
                "woody",
                "woody2",
                "woody2@codeit.com",
                "010-0000-0000"
        );
        System.out.println("유저 수정: "
                + updatedUser.getdisplayName() + "/"
                + updatedUser.getEmail() + "/"
                + updatedUser.getPhoneNumber()
        );

        // 삭제
        boolean deleted = userService.deleteUser("woody2");
        System.out.println("유저 삭제 성공?: " + deleted);

        List<User> foundUsersAfterDelete = userService.getallUser();
        System.out.println("유저 삭제 후(다건): " + foundUsersAfterDelete.size());
    }

    static void channelCRUDTest(ChannelService channelService, String ownerId) {
        // 생성
        Channel channel = new Channel("공지", "공지 채널입니다.", ownerId);
        boolean created = channelService.addChannel(channel);
        System.out.println("채널 생성 성공?: " + created);
        System.out.println("채널 생성: " + channel.getId());

        // 조회(UUID)
        Channel foundChannel = channelService.getChannelById(channel.getId());
        System.out.println("채널 조회(UUID): " + foundChannel.getId());

        // 조회(name)
        Channel foundByName = channelService.getChannelByName("공지");
        System.out.println("채널 조회(name): " + foundByName.getId());

        // 전체 조회
        List<Channel> foundChannels = channelService.getallChannels();
        System.out.println("채널 조회(다건): " + foundChannels.size());

        // 수정(oldName 기준)
        Channel updatedChannel = channelService.updateChannel("공지", "공지사항", null);
        System.out.println("채널 수정: "
                + updatedChannel.getChannelName() + "/"
                + updatedChannel.getDescription()
        );

        // 삭제(name 기준)
        boolean deleted = channelService.deleteChannel("공지사항");
        System.out.println("채널 삭제 성공?: " + deleted);

        List<Channel> foundChannelsAfterDelete = channelService.getallChannels();
        System.out.println("채널 삭제 후(다건): " + foundChannelsAfterDelete.size());
    }

    static void messageCRUDTest(MessageService messageService, Channel channel, User author) {
        // 생성 (Message는 channelId를 String으로 들고 있으니 UUID -> String 변환) [web:179]
        String channelId = channel.getId().toString();
        Message message = new Message(author.getdisplayName(), "안녕하세요.", channelId);

        boolean created = messageService.addMessage(message);
        System.out.println("메시지 생성 성공?: " + created);
        System.out.println("메시지 생성: " + message.getId());

        // 조회(단건: content)
        Message foundByContent = messageService.getContent("안녕하세요.");
        System.out.println("메시지 조회(content): " + (foundByContent == null ? "null" : foundByContent.getId()));

        // 조회(단건: channelId)
        Message foundByChannel = messageService.getChannelId(channelId);
        System.out.println("메시지 조회(channelId): " + (foundByChannel == null ? "null" : foundByChannel.getId()));

        // 조회(다건: username)
        List<Message> foundByUsername = messageService.getUsername(author.getdisplayName());
        System.out.println("메시지 조회(username 다건): " + foundByUsername.size());

        // 조회(다건: 전체)
        List<Message> foundMessages = messageService.getAllMessages();
        System.out.println("메시지 조회(전체 다건): " + foundMessages.size());

        // 수정(oldContent 기준)
        Message updated = messageService.updateMassage("안녕하세요.", "반갑습니다.", author.getdisplayName(), channelId);
        System.out.println("메시지 수정: " + (updated == null ? "null" : updated.getContent()));

        // 삭제
        // 네 인터페이스 deleteMessage(String message)는 지금 구현상 "content로 삭제"로 맞춰둔 상태라고 가정
        boolean deleted = messageService.deleteMessage("반갑습니다.");
        System.out.println("메시지 삭제 성공?: " + deleted);

        List<Message> foundAfterDelete = messageService.getAllMessages();
        System.out.println("메시지 삭제 후(전체 다건): " + foundAfterDelete.size());
    }

    static User setupUser(UserService userService) {
        User user = new User("woody", "woody@codeit.com", "010-1234-5678");
        userService.addUser(user);
        return user;
    }

    static Channel setupChannel(ChannelService channelService, String ownerId) {
        Channel channel = new Channel("공지", "공지 채널입니다.", ownerId);
        channelService.addChannel(channel);
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        String channelId = channel.getId().toString(); // [web:179]
        Message message = new Message(author.getdisplayName(), "안녕하세요.", channelId);
        messageService.addMessage(message);
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {
        // 서비스 초기화(네 파일 기반 구현체)
        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService();

        // 테스트(원하면 주석/해제)
        // userCRUDTest(userService);
        // channelCRUDTest(channelService, "owner-001");

        // 셋업
        User user = setupUser(userService);
        String ownerId = user.getId().toString(); // ownerId를 String으로 쓰는 구조라 변환 [web:179]
        Channel channel = setupChannel(channelService, ownerId);

        // 테스트
        messageCRUDTest(messageService, channel, user);
        // 또는 단순 생성만:
        // messageCreateTest(messageService, channel, user);
    }
}
