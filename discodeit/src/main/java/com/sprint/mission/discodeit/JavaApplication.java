package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.manager.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.jcf.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class JavaApplication {
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    public static void main(String[] args) throws InterruptedException {
        UserService userService = new JCFUserService();
        CategoryService categoryService = new JCFCategoryService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(userService, channelService);
        DiscordManager discordManager = new DiscordManager(userService, channelService, messageService, categoryService);

        System.out.println("========================================================================================");
        System.out.println("                                     테스트 시작");
        System.out.println("========================================================================================");

        // [1] 유저 생성
        System.out.println("\n[1] 유저 생성 (지헌, 지원, 채영)");
        userService.save(new User("지헌", "jiheon@fromis.com", "010-1111-2222"));
        userService.save(new User("지원", "jiwon@fromis.com", "010-2222-3333"));
        userService.save(new User("채영", "chaeyoung@fromis.com", "010-4444-5555"));
        printSystemSnapshot(userService, channelService, categoryService);

        // [2] 유저 수정
        System.out.println("\n[2] 유저 정보 수정 (지헌 -> 지헌_Update)");
        User jiheon = userService.findByDisplayName("지헌").get();
        jiheon.update("지헌_Update", "new_jiheon@test.com", "010-9999-9999");
        userService.update(jiheon);
        printSystemSnapshot(userService, channelService, categoryService);

        // [3] 구조적 데이터 생성
        System.out.println("\n[3] 구조적 데이터 생성 (카테고리, 채널)");
        Category cat1 = categoryService.save(new Category("공지사항"));
        Category cat2 = categoryService.save(new Category("자유게시판"));
        Channel chan1 = channelService.save(new Channel("StayThisWay", ChannelType.TEXT, "중요 공지", cat1));
        Channel chan2 = channelService.save(new Channel("LikeYouBetter", ChannelType.TEXT, "자유로운 대화", cat2));
        printSystemSnapshot(userService, channelService, categoryService);

        // [4] 메시지 전송
        System.out.println("\n[4] 다중 유저 메시지 전송");
        User user1 = userService.findByDisplayName("지헌_Update").get();
        User user3 = userService.findByDisplayName("채영").get();
        Message m1 = discordManager.sendMessage(user1.getId(), chan1.getId(), "매일이난 Sunday 월요일은 사라져.");
        Message m2 = discordManager.sendMessage(user3.getId(), chan1.getId(), "Stay this way 깊고 짙은 Blue");
        printMessageHistory(chan1.getId(), messageService, discordManager);

        // [5] 메시지 삭제 테스트
        System.out.println("\n[5] 특정 메시지 삭제 테스트");
        messageService.delete(m1.getId());
        printMessageHistory(chan1.getId(), messageService, discordManager);

        // [6] 유저 삭제 및 무결성 확인
        System.out.println("\n[6] 유저 '채영' 삭제 후 메시지 상태 확인");
        userService.delete(user3.getId());
        printSystemSnapshot(userService, channelService, categoryService);
        printMessageHistory(chan1.getId(), messageService, discordManager);

        // [7] 채널 삭제 및 메시지 보존 확인
        System.out.println("\n[7] 채널 'StayThisWay' 삭제 후 메시지 생존 확인");
        UUID orphanedMsgId = m2.getId();
        channelService.delete(chan1.getId());
        System.out.println("채널 'StayThisWay' 삭제완료.");
        printSystemSnapshot(userService, channelService, categoryService);

        System.out.println("\n더 이상 채널 메세지에서 확인 불가. \n따라서 삭제된 채널에 있던 메시지를 ID로 추적합니다...");
        messageService.findById(orphanedMsgId).ifPresentOrElse(
                m -> {
                    System.out.println("------------------------------------------------------------");
                    System.out.println("메시지 실체 확인 성공");
                    System.out.printf("메시지 내용: %s\n", m.getContent());
                    System.out.printf("작성자(ID 참조): %s\n", discordManager.getAuthorName(m.getId()));
                    System.out.printf("소속 채널 ID: %s (현재 존재하지 않는 채널)\n", m.getChannelId());
                    System.out.printf("생성 시각: %s\n", timeFormat.format(m.getCreatedAt()));
                    System.out.println("결론: 채널 엔티티는 삭제되었지만, 메시지는 안전하게 보존됨.");
                },
                () -> System.out.println("오류: 메시지가 채널과 함께 삭제되었습니다.")
        );

        // [8] 카테고리 삭제 및 채널 Orphan 확인
        System.out.println("\n[8] '자유게시판' 카테고리 삭제 -> 소속 채널 '미지정' 상태 확인");
        System.out.println("카테고리를 지워도 채널은 삭제되지 않고 Orphan되어야 함.");
        discordManager.deleteCategorySafely(cat2.getId());

        printSystemSnapshot(userService, channelService, categoryService);

        System.out.println("\n========================================================================================");
        System.out.println("                                       테스트 완료");
        System.out.println("========================================================================================");
    }

    private static void printSystemSnapshot(UserService us, ChannelService cs, CategoryService catS) {
        System.out.println("\n[유저 상세 목록]");
        System.out.printf("%-12s | %-20s | %-15s | %-12s\n", "Display Name", "Email", "Phone", "Created At");
        System.out.println("----------------------------------------------------------------------------------------");
        us.findAll().forEach(u -> System.out.printf("%-12s | %-20s | %-15s | %-12s\n",
                u.getDisplayName(), u.getEmail(), u.getPhoneNumber(), timeFormat.format(u.getCreatedAt())));

        System.out.println("\n[카테고리 및 채널 구조]");
        System.out.printf("%-10s | %-15s | %-15s | %-12s\n", "Type", "Name", "Belongs To", "Created At");
        System.out.println("----------------------------------------------------------------------------------------");
        catS.findAll().forEach(c -> System.out.printf("%-10s | %-15s | %-15s | %-12s\n",
                "Category", c.getName(), "-", timeFormat.format(c.getCreatedAt())));
        cs.findAll().forEach(c -> System.out.printf("%-10s | %-15s | %-15s | %-12s\n",
                "Channel", c.getName(), (c.getCategory() != null ? c.getCategory().getName() : "(미지정)"), timeFormat.format(c.getCreatedAt())));
        System.out.println("----------------------------------------------------------------------------------------");
    }

    private static void printMessageHistory(UUID channelId, MessageService ms, DiscordManager dm) {
        System.out.println("\n--- [채널 메시지 이력] ---");
        List<Message> msgs = ms.findByChannelId(channelId);
        if (msgs.isEmpty()) System.out.println("(출력할 메시지가 없습니다)");
        else {
            for (Message m : msgs) {
                System.out.printf("[%s] %-10s : %s\n",
                        timeFormat.format(m.getCreatedAt()), dm.getAuthorName(m.getId()), m.getContent());
            }
        }
        System.out.println("--------------------------");
    }
}