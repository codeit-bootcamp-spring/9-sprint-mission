import entity.*;
import service.*;
import service.jcf.*;
import java.util.*;

public class JavaApplication {

    private final UserService userService;
    private final MessageService messageService;
    private final ChannelService channelService;


    public JavaApplication(UserService userService, MessageService messageService, ChannelService channelService){
        this.userService = userService;
        this.messageService = messageService;
        this.channelService = channelService;
    }

    public void test(){
        // 채널 추가
        Channel ch1 = channelService.Create(ChannelType.PRIVATE, "게임모임");
        Channel ch2 = channelService.Create(ChannelType.PRIVATE, "안녕하세요");
        Channel ch3 = channelService.Create(ChannelType.PRIVATE, "반가워요");

        // 유저 추가
        User user1 = userService.Create("한성재", "0109999999", "abc@abc.com");
        channelService.addMember(ch1.getId(), user1);
        User user2 = userService.Create("james", "01011111111", "bb@ags.com");
        channelService.addMember(ch1.getId(), user2);
        User user3 = userService.Create("emily", "01022222222", "fgsd@ddf.com");
        channelService.addMember(ch1.getId(), user3);
        User user4 = userService.Create("michael", "01033334444", "a32@ggf.com");
        channelService.addMember(ch2.getId(), user4);
        User user5 = userService.Create("sophia", "01055552222", "hdw2344@abc.com");
        channelService.addMember(ch2.getId(), user5);

        // 메시지 추가
        Message msg1 = messageService.Create(user1.getId(), ch1.getId(), "안녕하세요 반갑습니다.");
        Message msg2 = messageService.Create(user2.getId(), ch1.getId(), "오늘 너무 춥네요.");
        Message msg3 = messageService.Create(user3.getId(), ch1.getId(), "저는 따뜻해요.");
        Message msg4 = messageService.Create(user1.getId(), ch1.getId(), "어제 뭐하셨어요.");
        Message msg5 = messageService.Create(user2.getId(), ch1.getId(), "그냥 잤어요.");
        Message msg6 = messageService.Create(user4.getId(), ch2.getId(), "어제 뭐드셨어요.");
        Message msg7 = messageService.Create(user5.getId(), ch2.getId(), "몰라도 됩니다.");

        // 조회
        System.out.println("단건 조회");
        ch3.PrintInfo();
        user1.PrintInfo();
        msg2.PrintInfo(userService.findByID(msg2.getWriter()), channelService.findByID(msg2.getChannel()));
        System.out.println("\n전체 조회");
        for (var ch : channelService.getAll()) {
            ch.PrintInfo();
        }
        System.out.println();
        for (var user : userService.getAll()) {
            user.PrintInfo();

        }
        System.out.println();
        List<Message> msgList = messageService.getAll().stream().filter(m -> m.getChannel().equals(ch1.getId())).toList();
        for (var msg : msgList) {
            msg.PrintInfo(userService.findByID(msg.getWriter()), channelService.findByID(msg.getChannel()));
        }
        System.out.println();

        // 수정
        System.out.println("수정 후 변경사항 출력");
        // 채널
        UUID chId1 = ch1.getId();
        channelService.updateName(chId1, "이제 게임 안하는 모임");
        // 유저
        UUID userId1 = user1.getId();
        userService.updateName(userId1,"SeongJae");
        userService.updatePhoneNumber(userId1, "01096450447");
        userService.updateEmail(userId1,"a5343@gmail.com");

        // 메시지
        UUID msgId1 = msg1.getId();
        messageService.modifyContent(msgId1, "안녕 못해요.");
        Message ms1 = messageService.findByID(msgId1);

        ch1.PrintInfo();
        user1.PrintInfo();
        ms1.PrintInfo(userService.findByID(ms1.getWriter()), channelService.findByID(ms1.getChannel()));
        System.out.println();

        // 삭제
        int ChannelSizeBeforeDelete = channelService.getAll().size();
        int UserSizeBeforeDelete = userService.getAll().size();
        int MessageSizeBeforeDelete = messageService.getAll().size();
        channelService.Remove(chId1);
        userService.Remove(userId1);
        messageService.Remove(msgId1);
        System.out.println("삭제 후 크기 비교");
        System.out.println("채널) 삭제 전: " + ChannelSizeBeforeDelete + " | 삭제 후: " + channelService.getAll().size());
        System.out.println("유저) 삭제 전: " + UserSizeBeforeDelete + " | 삭제 후: " + userService.getAll().size());
        System.out.println("메시지) 삭제 전: " + MessageSizeBeforeDelete + " | 삭제 후: " + messageService.getAll().size());
        System.out.println();
    }

    public static void main(String[] args){
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(channelService);

        JavaApplication japp = new JavaApplication(userService, messageService, channelService);

        // 일단 하드코딩으로 데이터 집어넣고 조회하자
        japp.test();
    }
}
