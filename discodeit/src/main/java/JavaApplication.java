import entity.Channel;
import entity.ChannelType;
import entity.Message;
import entity.User;
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
        Message msg1 = messageService.Create(user1, ch1, "안녕하세요 반갑습니다.");
        Message msg2 = messageService.Create(user2, ch1, "오늘 너무 춥네요.");
        Message msg3 = messageService.Create(user3, ch1, "저는 따뜻해요.");
        Message msg4 = messageService.Create(user1, ch1, "어제 뭐하셨어요.");
        Message msg5 = messageService.Create(user2, ch1, "그냥 잤어요.");
        Message msg6 = messageService.Create(user4, ch2, "어제 뭐드셨어요.");
        Message msg7 = messageService.Create(user5, ch2, "몰라도 됩니다.");

        // 전체 조회
        for (var ch : channelService.getAll()) {
            ch.PrintInfo();
        }
        System.out.println();
        for (var user : userService.getAll()) {
            user.PrintInfo();
        }
        System.out.println();
        for (var msg : messageService.getAll()) {
            msg.PrintInfo();
        }
        System.out.println();

        // 수정
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
        ms1.PrintInfo();
        System.out.println();

        // 삭제
        channelService.Remove(chId1);
        userService.removeUser(userId1);
        messageService.Remove(msgId1);
        for (var ch : channelService.getAll()) {
            ch.PrintInfo();
        }
        System.out.println();
        for (var user : userService.getAll()) {
            user.PrintInfo();
        }
        System.out.println();
        for (var msg : messageService.getAll()) {
            msg.PrintInfo();
        }

    }

//    public void register(String cmd, Scanner sc){
//        String next_cmd = "";
//        System.out.println("채널: 1 / 유저: 2 / 메시지: 3");
//        System.out.print("명령어를 입력하시오: ");
//        next_cmd = sc.nextLine();
//
//        switch(next_cmd) {
//            case "1": {
//                System.out.print("채널 이름을 입력하시오: ");
//                next_cmd = sc.nextLine();
//
//                UUID id = channelService.Create(ChannelType.PUBLIC, next_cmd);
//
//                System.out.println("등록 완료 | 채널 UUID: " + id.toString());
//
//            }break;
//            case "2": {
//                System.out.print("등록할 채널의 UUID를 입력하시오: ");
//                UUID ch_id = UUID.fromString(sc.nextLine());
//
//                if (channelService.findByID(ch_id) == null){
//                    System.out.print("해당 채널이 존재하지 않습니다. \n");
//                    return;
//                }
//
//                System.out.print("유저 이름을 입력하시오: ");
//                String name = sc.nextLine();
//                System.out.print("전화 번호을 입력하시오: ");
//                String num = sc.nextLine();
//                System.out.print("email을 입력하시오: ");
//                String email = sc.nextLine();
//
//                UUID id = userService.Create(name, num, email);
//                channelService.addMember(ch_id, id);
//                System.out.println("등록 완료 | 유저 UUID: " + id.toString());
//
//            }break;
//            case "3": {
//                System.out.print("채널 ID를 입력하시오: ");
//                UUID ch_id = UUID.fromString(sc.nextLine());
//                System.out.print("유저 ID를 입력하시오: ");
//                UUID user_id = UUID.fromString(sc.nextLine());
//
//                System.out.print("메시지 내용을 입력하시오: ");
//                String content = sc.nextLine();
//                messageService.Create(user_id, content);
//
//                channelService.Create(ChannelType.PUBLIC,next_cmd);
//            }break;
//        }
//    }
//
//    public void search(String cmd, Scanner sc) {
//        String next_cmd = "";
//        System.out.println("채널: 1 / 유저: 2 / 메시지: 3");
//        System.out.print("명령어를 입력하시오: ");
//        next_cmd = sc.nextLine();
//
//        switch(next_cmd) {
//            case "1": {
//                System.out.print("조회 할 채널의 UUID를 입력해주세요 (유저 전체 출력: a:");
//                String id = sc.nextLine();
//
//                if (Objects.equals(id, "a")){
//                    for (var c : channelService.getAll()){
//                        System.out.println("채널) UUID: " + c.getId() + " | 이름: " + c.getName() + " | 생성일자: " + c.getCreatedAt());
//                    }
//                }
//                Channel target = channelService.findByID(UUID.fromString(id));
//                System.out.println("채널) UUID: " + target.getId() + " | 이름: " + target.getName() + " | 생성일자: " + target.getCreatedAt());
//            }break;
//            case "2": {
//
//            }break;
//            case "3": {
//
//            }break;
//        }
//    }

    public static void main(String[] args){
        JavaApplication japp = new JavaApplication(new JCFUserService(), new JCFMessageService(), new JCFChannelService());

        // 일단 하드코딩으로 데이터 집어넣고 조회하자
        japp.test();
    }
}
