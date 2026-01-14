import entity.*;
import service.*;
import service.jcf.*;
import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        JCFChannelService jcfChannelService = new JCFChannelService();
        //SRP위반이슈: 순환참조문제를 여기서 해결하면 안되는데.. 어케함ㅜㅜ
        ChannelService channelService = jcfChannelService;
        MessageService messageService = new JCFMessageService(userService, channelService);
        jcfChannelService.setMessageService(messageService);

        System.out.println("\n1. 유저 및 채널 생성 테스트");
        User minju = new User("구민주", "minju@discodeit.com", "010-XXXX-XXXX");
        User friend = new User("친구", "friend@discodeit.com", "010-YYYY-YYYY");
        userService.save(minju);
        userService.save(friend);

        Category notice = new Category("공지");
        Channel general = new Channel("일반-공지", ChannelType.TEXT, null, notice);
        channelService.save(general);

        System.out.println("성공: 현재 등록된 유저 수 = " + userService.findAll().size());
        System.out.println("성공: 현재 등록된 채널 수 = " + channelService.findAll().size());

        System.out.println("\n2. 이름 검색 최적화 테스트");
        userService.findByDisplayName("구민주").ifPresentOrElse(
                u -> System.out.println("검색 성공: [" + u.getDisplayName() + "] 님을 찾았습니다."),
                () -> System.out.println("오류: 유저를 찾지 못했습니다.")
        );
        userService.findByDisplayName("없는사람").ifPresentOrElse(
                u -> System.out.println("검색 성공: [" + u.getDisplayName() + "] 님을 찾았습니다."),
                () -> System.out.println("오류: 유저를 찾지 못했습니다.")
        );

        System.out.println("\n3. 메시지 그룹화 조회 테스트");
        Message msg1 = messageService.save(new Message("일반공지1...", minju.getId(), general.getId()));
        messageService.save(new Message("일반공지2...", friend.getId(), general.getId()));

        System.out.println("성공: [" + general.getName() + "] 채널 메시지 등록 완료");
        List<Message> generalMessages = messageService.findByChannelId(general.getId());
        System.out.println("성공: [" + general.getName() + "] 채널 메시지 개수 = " + generalMessages.size());

        System.out.println("\n4. 데이터 수정 테스트");
        minju.update("구민주아님", minju.getEmail(), "010-ZZZZ-ZZZZ");
        userService.update(minju);

        User updatedMinju = userService.findById(minju.getId()).get();
        System.out.println("확인: 수정된 이름 = " + updatedMinju.getDisplayName());

        System.out.println("\n5. [심화] 유저 삭제 및 Unknown 작성자 테스트");
        userService.delete(minju.getId());

        Message lastMsg = messageService.findByChannelId(general.getId()).get(0);
        String author = messageService.getAuthorName(msg1.getId());
        System.out.println("작성자: " + author);

        System.out.println("\n6. [심화] 채널 삭제 시 데이터 무결성 테스트");
        System.out.println("- 삭제 전 메시지 존재: " + !messageService.findByChannelId(general.getId()).isEmpty());

        channelService.delete(general.getId());
        System.out.println("- 채널 '일반-공지' 삭제됨.");

        List<Message> leftover = messageService.findByChannelId(general.getId());
        System.out.println("- 삭제 후 메시지 존재: " + !messageService.findByChannelId(general.getId()).isEmpty());
        if (leftover.isEmpty()) {
            System.out.println("- 결과: 채널 삭제 시 모든 메시지가 성공적으로 정리되었습니다. (PASS)");
        } else {
            System.out.println("- 결과: 메시지가 여전히 남아있습니다. (FAIL)");
        }
        System.out.println("\n========= [테 완] =========");
    }
}